package ohhhhhh.dc.db;

import ohhhhhh.dc.AbstractConfigDescription;
import ohhhhhh.dc.AbstractConfigPostProcessor;
import ohhhhhh.dc.CommonConfigDescription;
import ohhhhhh.dc.ConfigRegistry;
import org.springframework.beans.BeansException;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * 数据库类型的后置处理器
 *
 * @author fzh
 * @since 1.0
 */
public class DbConfigPostProcessor extends AbstractConfigPostProcessor<DbConfig> implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public DbConfigPostProcessor(ConfigRegistry registry) {
        super(registry, DbConfig.class);
    }

    @Override
    protected AbstractConfigDescription resolveConfigDescription(String configName, Object bean, DbConfig dbConfig) {
        String defaultDataSource = dbConfig.defaultDataSource();
        String defaultQuerySql = dbConfig.defaultQuerySql();
        DataSource dataSource;
        if (StringUtils.isEmpty(defaultDataSource)) {
            dataSource = applicationContext.getBean(DataSource.class);
        } else {
            dataSource = applicationContext.getBean(defaultDataSource, DataSource.class);
        }
        Object enhanced = Enhancer.create(bean.getClass(), new DbBeanGetterInterceptor(bean, dbConfig.treatMemberNameAsPlaceholder(), dbConfig.callInitiatedIfNotFound(),
                defaultQuerySql, dataSource, s -> applicationContext.getBean(s, DataSource.class)));
        return new CommonConfigDescription(configName, enhanced);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
