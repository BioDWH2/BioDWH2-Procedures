package de.unibi.agbi.biodwh2.procedures;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Procedures.class)
public @interface Procedure {
    String name() default "";

    String description() default "";
}

