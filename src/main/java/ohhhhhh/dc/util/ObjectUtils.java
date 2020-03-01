package ohhhhhh.dc.util;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author fzh
 * @since 1.0
 */
public final class ObjectUtils extends Utils {

    @Nullable
    public static Object retrieve(@NonNull Object object, @NonNull PropertyDescriptor descriptor) {
        Method getter = descriptor.getReadMethod();
        if (getter == null) {
            Field field = ReflectionUtils.findField(object.getClass(), descriptor.getName());
            if (field == null) {
                return null;
            }

            if (!field.isAccessible()) {
                ReflectionUtils.makeAccessible(field);
            }
            return ReflectionUtils.getField(field, object);
        } else {
            if (!getter.isAccessible()) {
                ReflectionUtils.makeAccessible(getter);
            }
            return ReflectionUtils.invokeMethod(getter, object);
        }
    }

    public static void padding(@NonNull Object object, @NonNull PropertyDescriptor descriptor, @Nullable Object arg) {
        Method setter = descriptor.getWriteMethod();
        if (setter == null) {
            Field field = ReflectionUtils.findField(object.getClass(), descriptor.getName());
            if (field == null) {
                return;
            }
            if (!field.isAccessible()) {
                ReflectionUtils.makeAccessible(field);
            }
            Class<?> fieldType = field.getType();
            if (arg == null || fieldType.isInstance(arg)) {
                ReflectionUtils.setField(field, object, arg);
            }
        } else {
            if (!setter.isAccessible()) {
                ReflectionUtils.makeAccessible(setter);
            }
            MethodParameter methodParameter = BeanUtils.getWriteMethodParameter(descriptor);
            if (arg == null || methodParameter.getParameterType().isInstance(arg)) {
                ReflectionUtils.invokeMethod(setter, object, arg);
            }
        }
    }

}
