package ohhhhhh.dc.db;

import ohhhhhh.dc.Property;
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
@Target(ElementType.FIELD)
public @interface DbProperty {

    @AliasFor(annotation = Property.class)
    String value() default "";

    String querySql() default "";

    String dataSource() default "";

}
