package ohhhhhh.dc.config;

import ohhhhhh.dc.ConfigRegistry;
import ohhhhhh.dc.db.DbConfigPostProcessor;
import ohhhhhh.dc.file.FileConfigPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author fzh
 * @since 1.0
 */
@Configuration
public class DynamicConfigConfiguration {

    @Bean
    @ConditionalOnMissingBean(ConfigRegistry.class)
    public ConfigRegistry configRegistry() {
        return new ConfigRegistry();
    }

    @Bean
    @ConditionalOnMissingBean(FileConfigPostProcessor.class)
    public FileConfigPostProcessor fileConfigPostProcessor(ConfigRegistry configRegistry) {
        return new FileConfigPostProcessor(configRegistry);
    }

    @Bean
    @ConditionalOnMissingBean(DbConfigPostProcessor.class)
    @ConditionalOnBean(DataSource.class)
    public DbConfigPostProcessor dbConfigPostProcessor(ConfigRegistry configRegistry, DataSource dataSource) {
        return new DbConfigPostProcessor(configRegistry, dataSource);
    }
}
