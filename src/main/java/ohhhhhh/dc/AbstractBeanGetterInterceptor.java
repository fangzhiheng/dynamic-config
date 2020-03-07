package ohhhhhh.dc;


import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fzh
 * @since 1.0
 */
public abstract class AbstractBeanGetterInterceptor implements MethodInterceptor {

    public static final String GETTER_PREFIX = "get";

    private final Object target;

    private final boolean treatMemberNameAsPlaceholder;

    private final boolean callInitiatedIfNotFound;

    private final Map<Method, PropertyAnnotationInfo> getterFieldMapping = new ConcurrentHashMap<>();

    private final ConfigurableConversionService conversionService;

    public AbstractBeanGetterInterceptor(Object target, boolean treatMemberNameAsPlaceholder, boolean callInitiatedIfNotFound, ConfigurableConversionService conversionService) {
        Assert.notNull(target, "intercepted bean must not be null");
        this.target = target;
        this.treatMemberNameAsPlaceholder = treatMemberNameAsPlaceholder;
        this.callInitiatedIfNotFound = callInitiatedIfNotFound;
        this.conversionService = conversionService;
    }

    public AbstractBeanGetterInterceptor(Object target, boolean treatMemberNameAsPlaceholder, boolean callInitiatedIfNotFound) {
        this(target, treatMemberNameAsPlaceholder, callInitiatedIfNotFound, new DefaultConversionService());
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if (isGetter(method)) {
            PropertyAnnotationInfo annotationInfo = getCachedAnnotationInfo(method);
            return invoke(target, annotationInfo, objects);
        }
        return method.invoke(target, objects);
    }

    protected abstract Object invoke(@NonNull Object target, PropertyAnnotationInfo annotationInfo, @NonNull Object[] parameters);

    private boolean isGetter(Method method) {
        return method.getName().startsWith(GETTER_PREFIX);
    }

    private PropertyAnnotationInfo getCachedAnnotationInfo(Method method) {
        return getterFieldMapping.computeIfAbsent(method, m -> {
            String fieldName = Introspector.decapitalize(m.getName().replace(GETTER_PREFIX, ""));
            Field field = ReflectionUtils.findField(target.getClass(), fieldName);
            return new PropertyAnnotationInfo(field, method, treatMemberNameAsPlaceholder);
        });
    }

    public Object getTarget() {
        return target;
    }

    public boolean isTreatMemberNameAsPlaceholder() {
        return treatMemberNameAsPlaceholder;
    }

    public boolean isCallInitiatedIfNotFound() {
        return callInitiatedIfNotFound;
    }

    public ConfigurableConversionService getConversionService() {
        return conversionService;
    }

    protected static class PropertyAnnotationInfo extends AnnotationInfo<Property> {

        private final String name;

        public PropertyAnnotationInfo(@Nullable Field field, Method method, boolean useMemberName) {
            super(field, method, Property.class);
            String name = null;
            Property annotation = getAnnotation();
            if (annotation != null) {
                name = annotation.value();
            }
            if (StringUtils.isEmpty(name) && useMemberName) {
                if (field != null) {
                    name = field.getName();
                } else {
                    name = method.getName();
                }
            }
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}
