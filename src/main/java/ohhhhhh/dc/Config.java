package ohhhhhh.dc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置注解
 *
 * @author fzh
 * @see CommonConfigDescription
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

    String name() default "";

    /**
     * @return 是否把字段或者方法名作为占位符
     */
    boolean treatMemberNameAsPlaceholder() default false;

    /**
     * @return 是否在没有找到占位符的时候使用原始方法返回值
     */
    boolean callInitiatedIfNotFound() default true;

}
