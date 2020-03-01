package ohhhhhh.dc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * @author fzh
 * @since 1.0
 */
public abstract class AbstractConfigPostProcessor<A extends Annotation> implements BeanPostProcessor {

    private final ConfigRegistry registry;

    private Class<A> supportedAnnotationClass;

    protected AbstractConfigPostProcessor(ConfigRegistry registry) {
        this(registry, null);
    }

    protected AbstractConfigPostProcessor(ConfigRegistry registry, Class<A> supportedAnnotationClass) {
        this.supportedAnnotationClass = supportedAnnotationClass;
        Objects.requireNonNull(registry, "ConfigRegistry is necessary for " + getClass().getSimpleName());
        this.registry = registry;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        A supportedAnnotation = null;
        if (supportedAnnotationClass == null || (supportedAnnotation = AnnotatedElementUtils.findMergedAnnotation(bean.getClass(), supportedAnnotationClass)) != null) {
            Config configAnnotation = AnnotatedElementUtils.findMergedAnnotation(bean.getClass(), Config.class);
            if (configAnnotation != null) {
                Object enhancedConfigBean = enhanceConfigBean(beanName, bean, supportedAnnotation);
                String configName = configAnnotation.name();
                if (StringUtils.isEmpty(configName)) {
                    configName = beanName;
                }
                AbstractConfigDescription description = resolveConfigDescription(configName, enhancedConfigBean);
                registry.register(configName, description);
                return enhancedConfigBean;
            }
        }
        return bean;
    }

    protected abstract Object enhanceConfigBean(String beanName, Object bean, A configAnnotation);

    protected abstract AbstractConfigDescription resolveConfigDescription(String configName, Object enhancedConfigBean);

}
