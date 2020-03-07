package ohhhhhh.dc.file;

import ohhhhhh.dc.AbstractBeanGetterInterceptor;
import ohhhhhh.dc.ReloadablePropertySource;
import ohhhhhh.dc.util.PlaceholderUtils;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author fzh
 * @since 1.0
 */
public class FileBeanGetterInterceptor extends AbstractBeanGetterInterceptor {

    private final ReloadablePropertySource reloadablePropertySource;

    public FileBeanGetterInterceptor(Object target, boolean treatUnannotatedFieldAsName, boolean callInitiatedIfNotFound, ReloadablePropertySource reloadablePropertySource) {
        super(target, treatUnannotatedFieldAsName, callInitiatedIfNotFound);
        this.reloadablePropertySource = reloadablePropertySource;
    }

    @Override
    protected Object invoke(@NonNull Object target, PropertyAnnotationInfo annotationInfo, @NonNull Object[] parameters) {
        String name = annotationInfo.getName();
        Method method = annotationInfo.getMethod();
        if (StringUtils.isEmpty(name)) {
            if (isCallInitiatedIfNotFound()) {
                return ReflectionUtils.invokeMethod(method, target, parameters);
            } else {
                throw new IllegalStateException("call initiated bean method not enabled, but not found a valid name, what do you mean?");
            }
        }
        ConfigurableConversionService conversionService = getConversionService();
        String placeholder = PlaceholderUtils.getPlaceholder(name);
        if (placeholder == null) {
            placeholder = name;
        }
        Class<?> returnType = method.getReturnType();
        Object value = reloadablePropertySource.getProperty(placeholder);
        if (value != null && conversionService.canConvert(value.getClass(), returnType)) {
            value = conversionService.convert(value, returnType);
        } else if (value == null) {
            String defaultValue = PlaceholderUtils.getDefault(name);
            if (defaultValue != null && conversionService.canConvert(String.class, returnType)) {
                value = conversionService.convert(defaultValue, returnType);
            }
        }
        if (value != null && !returnType.isInstance(value)) {
            throw new IllegalStateException("return type is " + returnType + ", but found result " + value);
        }
        return value;
    }

}
