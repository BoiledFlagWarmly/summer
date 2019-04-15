package org.summer.warm.annotations;

import java.lang.annotation.*;

@Target({ElementType.CONSTRUCTOR, ElementType.METHOD,
        ElementType.PARAMETER, ElementType.FIELD,
        ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

    String value() default "";

}
