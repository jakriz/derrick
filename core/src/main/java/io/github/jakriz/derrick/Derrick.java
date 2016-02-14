package io.github.jakriz.derrick;

import io.github.jakriz.derrick.processor.Namer;

public class Derrick {

    public static <T> T get(Class<T> klass) {
        try {
            Class<?> derrickKlass = Class.forName(Namer.generatedQualifiedName(klass));
            return (T) derrickKlass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }
}
