package ohhhhhh.dc.file;

import ohhhhhh.dc.AbstractConfigPostProcessor;
import ohhhhhh.dc.Config;
import ohhhhhh.dc.ConfigDescription;
import ohhhhhh.dc.ConfigRegistry;

/**
 * TODO
 *
 * @author fzh
 * @since 1.0
 */
public class FileConfigPostProcessor extends AbstractConfigPostProcessor {

    public FileConfigPostProcessor(ConfigRegistry registry) {
        super(registry);
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
