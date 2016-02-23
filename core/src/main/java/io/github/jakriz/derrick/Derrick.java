package io.github.jakriz.derrick;

import io.github.jakriz.derrick.processor.Namer;

/**
 * The entry point to the library. Used to get the
 * implemented instances of the defined @DerrickInterface interfaces.
 */
public class Derrick {

    /**
     * Returns an instance of {@param klass} interface implemented by Derrick.
     */
    public static <T> T get(Class<T> klass) {
        try {
            Class<?> derrickKlass = Class.forName(Namer.generatedQualifiedName(klass));
            return (T) derrickKlass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

}
