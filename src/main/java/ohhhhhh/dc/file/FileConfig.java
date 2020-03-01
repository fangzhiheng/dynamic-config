package ohhhhhh.dc.file;

import ohhhhhh.dc.Config;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 文件类型配置注解
 *
 * @author fzh
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Config
public @interface FileConfig {

    @AliasFor(annotation = Config.class)
    String name();

    String[] locations() default {};

}
