package io.github.jakriz.derrick.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates an interface which contains methods to be implemented by downloading code.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface DerrickInterface {

    /**
     * The base URL from which to download.
     */
    String baseUrl();

    /**
     * The list of imports which to use with for the implementation class.
     */
    String[] imports();
}
