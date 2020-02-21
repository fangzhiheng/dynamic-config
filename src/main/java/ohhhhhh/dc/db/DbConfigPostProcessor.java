package ohhhhhh.dc.db;

import ohhhhhh.dc.AbstractConfigPostProcessor;
import ohhhhhh.dc.Config;
import ohhhhhh.dc.ConfigDescription;
import ohhhhhh.dc.ConfigRegistry;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * TODO
 *
 * @author fzh
 * @since 1.0
 */
public class DbConfigPostProcessor extends AbstractConfigPostProcessor {

    private final DataSource dataSource;

    public DbConfigPostProcessor(ConfigRegistry registry, DataSource dataSource) {
        super(registry);
        Objects.requireNonNull(dataSource, "DataSource is necessary for " + getClass().getSimpleName());
        this.dataSource = dataSource;
    }

    @Override
    protected Object enhanceConfigBean(Object bean, Config configAnnotation) {
        return null;
    }

    @Override
    protected ConfigDescription resolveConfigDescription(String configName, Object enhancedConfigBean) {
        return null;
    }
}
