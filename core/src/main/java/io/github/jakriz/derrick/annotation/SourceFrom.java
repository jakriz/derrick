package io.github.jakriz.derrick.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a method which should be implemented by downloading the code.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface SourceFrom {

    /**
     * The URL path from which to download code for this method.
     * Together with interface's base URL make the complete URL.
     */
    String path() default "";

    /**
     * The CSS selector identifying the element of the page with code to download.
     */
    String selector();

    /**
     * If true then a return statement will be added on the last line of the code.
     */
    boolean returnLast() default false;

    /**
     * If non-empty then a return statement with the content will
     * be added at the end of the code.
     */
    String addReturn() default "";
}
