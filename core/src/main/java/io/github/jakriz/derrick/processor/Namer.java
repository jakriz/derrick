package io.github.jakriz.derrick.processor;

import javax.lang.model.element.TypeElement;

public final class Namer {
    private static final String GENERATED_SUFFIX = "DerrickImpl";

    private Namer() { }

    public static String generatedClassName(TypeElement element) {
        return element.getSimpleName().toString() + GENERATED_SUFFIX;
    }

    public static String generatedPackageName(TypeElement element) {
        String qualifiedName = element.getQualifiedName().toString();
        return qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
    }

    public static String generatedQualifiedName(TypeElement element) {
        return generatedPackageName(element) + "." + generatedClassName(element);
    }

    public static String generatedQualifiedName(Class klass) {
        return klass.getCanonicalName() + GENERATED_SUFFIX;
    }

}
