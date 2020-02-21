package ohhhhhh.dc.config;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.MultiValueMap;

import java.util.LinkedList;
import java.util.List;

/**
 * Simple version of SpringBoot's {@code BeanCondition}
 *
 * @author fzh
 * @since 1.0
 */
public class BeanCondition implements Condition {

    @Override
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        List<Boolean> matchResult = new LinkedList<>();
        if (metadata.isAnnotated(ConditionalOnMissingBean.class.getName())) {
            matchResult.add(missingBeanMatches(context, metadata));
        }
        if (metadata.isAnnotated(ConditionalOnBean.class.getName())) {
            matchResult.add(beanMatches(context, metadata));
        }
        return matchResult.stream().allMatch(Boolean::booleanValue);
    }

    private boolean missingBeanMatches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(ConditionalOnMissingBean.class.getName());
        if (attrs != null) {
            for (Object value : attrs.get("value")) {
                Class<?>[] types = (Class<?>[]) value;
                ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
                for (Class<?> type : types) {
                    String[] beanNames = beanFactory.getBeanNamesForType(type);
                    for (String beanName : beanNames) {
                        if (beanFactory.containsBean(beanName)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private Boolean beanMatches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(ConditionalOnBean.class.getName());
        if (attrs != null) {
            for (Object value : attrs.get("value")) {
                Class<?>[] types = (Class<?>[]) value;
                ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
                for (Class<?> type : types) {
                    String[] beanNames = beanFactory.getBeanNamesForType(type);
                    if (beanNames.length == 0) {
                        return false;
                    }
                    for (String beanName : beanNames) {
                        if (!beanFactory.containsBean(beanName)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
}
