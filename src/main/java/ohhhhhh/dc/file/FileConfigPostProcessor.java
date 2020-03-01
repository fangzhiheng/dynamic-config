package ohhhhhh.dc.file;

import ohhhhhh.dc.AbstractConfigDescription;
import ohhhhhh.dc.AbstractConfigPostProcessor;
import ohhhhhh.dc.ConfigRegistry;
import ohhhhhh.dc.ReloadablePropertySource;
import ohhhhhh.dc.util.ResourceUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.core.io.Resource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author fzh
 * @since 1.0
 */
public class FileConfigPostProcessor extends AbstractConfigPostProcessor<FileConfig> {

    public FileConfigPostProcessor(ConfigRegistry registry) {
        super(registry, FileConfig.class);
    }

    @Override
    protected Object enhanceConfigBean(String beanName, Object bean, FileConfig configAnnotation) {
        String[] locations = configAnnotation.locations();
        if (locations.length != 0) {
            List<Resource> resources = Arrays.stream(locations)
                    .map(ResourceUtils::getResource)
                    .filter(Resource::exists)
                    .collect(Collectors.toList());
            try {
                ReloadablePropertySource reloadablePropertySource = new FileReloadablePropertySource("", resources);
                // TODO
            } catch (Exception e) {
                throw new BeanInitializationException("exception happened when create bean " + beanName, e);
            }
        }
        return bean;
    }

    @Override
    protected AbstractConfigDescription resolveConfigDescription(String configName, Object enhancedConfigBean) {
        return null;
    }

    private List<Map<String, Object>> loadAsMap(List<Resource> resources) {
        return Collections.emptyList();
    }

}
