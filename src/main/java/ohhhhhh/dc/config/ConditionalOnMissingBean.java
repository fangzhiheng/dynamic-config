package ohhhhhh.dc.config;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Similar to SpringBoot's {@code ConditionalOnMissingBean}
 *
 * @author fzh
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(BeanCondition.class)
public @interface ConditionalOnMissingBean {

    Class<?>[] value() default {};

}
