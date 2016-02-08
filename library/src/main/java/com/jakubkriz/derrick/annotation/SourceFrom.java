package com.jakubkriz.derrick.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface SourceFrom {

    String path() default "";

    String selector();

    boolean addReturn() default false;
}
