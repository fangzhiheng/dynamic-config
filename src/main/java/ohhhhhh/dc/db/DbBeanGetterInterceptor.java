package ohhhhhh.dc.db;

import ohhhhhh.dc.AbstractBeanGetterInterceptor;
import ohhhhhh.dc.util.PlaceholderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author fzh
 * @since 1.0
 */
public class DbBeanGetterInterceptor extends AbstractBeanGetterInterceptor {

    private final static Logger logger = LoggerFactory.getLogger(DbBeanGetterInterceptor.class);

    private final Map<PropertyAnnotationInfo, QueryInfo> queryInfoMapping = new ConcurrentHashMap<>();

    private final String defaultQuerySql;

    private final Function<String, DataSource> dataSourceResolver;

    private final DataSource defaultDataSource;

    public DbBeanGetterInterceptor(Object target, boolean treatMemberNameAsPlaceholder, boolean callInitiatedIfNotFound,
                                   String defaultQuerySql, DataSource defaultDataSource,
                                   Function<String, DataSource> dataSourceResolver) {
        super(target, treatMemberNameAsPlaceholder, callInitiatedIfNotFound);
        this.defaultQuerySql = checkAndNormalizeQuerySql(defaultQuerySql);
        this.defaultDataSource = defaultDataSource;
        this.dataSourceResolver = dataSourceResolver;
    }

    @Override
    protected Object invoke(@NonNull Object target, PropertyAnnotationInfo annotationInfo, @NonNull Object[] parameters) throws SQLException {
        String name = annotationInfo.getName();
        if (name == null) {
            if (isCallInitiatedIfNotFound()) {
                return ReflectionUtils.invokeMethod(annotationInfo.getMethod(), target, parameters);
            }
            throw new IllegalStateException("couldn't found property name for field[" + annotationInfo.getField() + "], method[" + annotationInfo.getMethod() + "]");
        }
        String placeholder = PlaceholderUtils.getPlaceholder(name);
        String[] queryParameters;
        if (placeholder != null) {
            queryParameters = placeholder.split(",");
        } else {
            queryParameters = name.split(",");
        }
        QueryInfo queryInfo = queryInfoMapping.computeIfAbsent(annotationInfo, ai -> {
            String sql = defaultQuerySql;
            DataSource dataSource = defaultDataSource;
            DbProperty dbProperty = null;
            switch (ai.getAnnotatedType()) {
                case ON_FIELD:
                    dbProperty = AnnotatedElementUtils.findMergedAnnotation(ai.getField(), DbProperty.class);
                    break;
                case ON_METHOD:
                    dbProperty = AnnotatedElementUtils.findMergedAnnotation(ai.getMethod(), DbProperty.class);
                    break;
            }
            if (dbProperty != null) {
                if (!StringUtils.isEmpty(dbProperty.querySql())) {
                    sql = checkAndNormalizeQuerySql(dbProperty.querySql());
                }
                if (!StringUtils.isEmpty(dbProperty.dataSource())) {
                    dataSource = dataSourceResolver.apply(dbProperty.dataSource());
                }
            }
            return new QueryInfo(sql, queryParameters, dataSource);
        });
        Class<?> returnType = annotationInfo.getMethod().getReturnType();
        Object result = queryInfo.doQuery();
        ConfigurableConversionService conversionService = getConversionService();
        if (conversionService.canConvert(result.getClass(), returnType)) {
            result = conversionService.convert(result, returnType);
        }
        return result;
    }

    private String checkAndNormalizeQuerySql(String sql) {
        String str = StringUtils.trimWhitespace(sql);
        if (!StringUtils.hasLength(str)) {
            throw new IllegalQuerySqlException("Illegal query sql format: " + sql);
        }
        return str;
    }

    private static class QueryInfo {

        String sql;

        String[] arguments;

        DataSource dataSource;

        String joinedParameters;

        QueryInfo(String sql, String[] arguments, DataSource dataSource) {
            this.sql = sql;
            this.arguments = arguments;
            this.dataSource = dataSource;
            joinedParameters = String.join(",", arguments);
        }

        Object doQuery() throws SQLException {
            try (Connection connection = dataSource.getConnection()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("query db config:\n" +
                            "Query SQL : [{}]\n" +
                            "Parameters: [{}]", sql, joinedParameters);
                }
                PreparedStatement stmt = connection.prepareStatement(sql);
                for (int i = 0; i < arguments.length; i++) {
                    stmt.setString(i + 1, arguments[i]);
                }
                ResultSet resultSet = stmt.executeQuery();
                ResultSetMetaData metaData = resultSet.getMetaData();
                if (metaData.getColumnCount() > 1) {
                    throw new IllegalStateException(metaData.getColumnCount() + " columns found, but only support single property this version, pick an exact one?");
                }
                if (resultSet.next()) {
                    return resultSet.getObject(1);
                }
            }
            return null;
        }

    }

}
