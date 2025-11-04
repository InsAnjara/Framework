package mg.itu.framework.servlet.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface URLMapping {
    String url();
    String method() default "GET";
}