package ohhhhhh.dc.db;

import ohhhhhh.dc.Config;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fzh
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Config
public @interface DbConfig {

    @AliasFor(annotation = Config.class)
    String name() default "";

    String sql();
}
