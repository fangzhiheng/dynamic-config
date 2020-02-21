package ohhhhhh.dc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author fzh
 * @since 1.0
 */
public abstract class AbstractConfigPostProcessor implements BeanPostProcessor {

    private final ConfigRegistry registry;

    protected AbstractConfigPostProcessor(ConfigRegistry registry) {
        Objects.requireNonNull(registry, "ConfigRegistry is necessary for " + getClass().getSimpleName());
        this.registry = registry;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Config configAnnotation = AnnotatedElementUtils.findMergedAnnotation(bean.getClass(), Config.class);
        if (configAnnotation != null) {
            Object enhancedConfigBean = enhanceConfigBean(bean, configAnnotation);
            String configName = configAnnotation.name();
            if (StringUtils.isEmpty(configName)) {
                configName = beanName;
            }
            ConfigDescription description = resolveConfigDescription(configName, enhancedConfigBean);
            registry.register(configName, description);
            return enhancedConfigBean;
        }
        return bean;
    }

    protected abstract Object enhanceConfigBean(Object bean, Config configAnnotation);

    protected abstract ConfigDescription resolveConfigDescription(String configName, Object enhancedConfigBean);
}
