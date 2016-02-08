package com.jakubkriz.derrick;

import com.jakubkriz.derrick.processor.Namer;

public class Derrick {

    public static <T> T get(Class<T> klass) {
        try {
            Class<?> derrickKlass = Class.forName(Namer.generatedQualifiedName(klass));
            return (T) derrickKlass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }
}
