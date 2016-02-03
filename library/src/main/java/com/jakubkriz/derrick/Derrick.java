package com.jakubkriz.derrick;

import java.lang.reflect.Constructor;

public class Derrick {

    public static <T> T get(Class<T> klass) {
        try {
            Class<?> derrickKlass = Class.forName(klass.getPackage().getName() + "." + klass.getSimpleName() + "DerrickImpl");
            return (T) derrickKlass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }
}
