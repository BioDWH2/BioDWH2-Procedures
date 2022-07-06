package de.unibi.agbi.biodwh2.procedures;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Functions.class)
public @interface Function {
    String name() default "";

    String signature() default "";

    String description() default "";
}

