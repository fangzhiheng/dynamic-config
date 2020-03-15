package ohhhhhh.dc.config;

import ohhhhhh.dc.ConfigRegistry;
import ohhhhhh.dc.db.DbConfigPostProcessor;
import ohhhhhh.dc.file.FileConfigPostProcessor;
import ohhhhhh.dc.util.FileWatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public FileConfigPostProcessor fileConfigPostProcessor(ConfigRegistry configRegistry, FileWatcher fileWatcher) {
        return new FileConfigPostProcessor(configRegistry, fileWatcher);
    }

    @Bean
    @ConditionalOnMissingBean(DbConfigPostProcessor.class)
    public DbConfigPostProcessor dbConfigPostProcessor(ConfigRegistry configRegistry) {
        return new DbConfigPostProcessor(configRegistry);
    }

    @Bean
    public FileWatcher fileWatcher() {
        return FileWatcher.getFileWatcher();
    }

}
