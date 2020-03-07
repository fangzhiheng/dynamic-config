package ohhhhhh.dc;

import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author fzh
 * @since 1.0
 */
public class AnnotationInfo<T extends Annotation> {

    private final Field field;

    private final Method method;

    private final AnnotatedType annotatedType;

    private final T annotation;

    public AnnotationInfo(@Nullable Field field, Method method, Class<T> annotationClass) {
        this.field = field;
        this.method = method;
        T annotation = null;
        AnnotatedType annotatedType = null;
        if (field != null) {
            annotation = field.getAnnotation(annotationClass);
            annotatedType = AnnotatedType.ON_FIELD;
        }
        if (annotation == null) {
            annotation = method.getAnnotation(annotationClass);
            annotatedType = AnnotatedType.ON_METHOD;
        }
        if (annotation == null) {
            annotatedType = AnnotatedType.NONE;
        }
        this.annotatedType = annotatedType;
        this.annotation = annotation;
    }

    public Field getField() {
        return field;
    }

    public Method getMethod() {
        return method;
    }

    public AnnotatedType getAnnotatedType() {
        return annotatedType;
    }

    public T getAnnotation() {
        return annotation;
    }

    /**
     * Indicate where the annotation annotated.
     */
    public enum AnnotatedType {
        /**
         * Annotated on Field
         */
        ON_FIELD,
        /**
         * Annotated on Getter
         */
        ON_METHOD,
        /**
         * No Annotation
         */
        NONE
    }

}
