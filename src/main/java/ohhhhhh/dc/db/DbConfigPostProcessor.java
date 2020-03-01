package ohhhhhh.dc.db;

import ohhhhhh.dc.AbstractConfigDescription;
import ohhhhhh.dc.AbstractConfigPostProcessor;
import ohhhhhh.dc.ConfigRegistry;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * TODO
 *
 * @author fzh
 * @since 1.0
 */
public class DbConfigPostProcessor extends AbstractConfigPostProcessor<DbConfig> {

    private final DataSource dataSource;

    public DbConfigPostProcessor(ConfigRegistry registry, DataSource dataSource) {
        super(registry, DbConfig.class);
        Objects.requireNonNull(dataSource, "DataSource is necessary for " + getClass().getSimpleName());
        this.dataSource = dataSource;
    }

    @Override
    protected Object enhanceConfigBean(String beanName, Object bean, DbConfig configAnnotation) {
        return null;
    }

    @Override
    protected AbstractConfigDescription resolveConfigDescription(String configName, Object enhancedConfigBean) {
        return null;
    }

}
