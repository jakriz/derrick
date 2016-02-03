package com.jakubkriz.derrick.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ClassGenerator {

    public void generate(TypeElement klass, List<MethodDefinition> methodDefinitions, Filer filer) throws IOException {
        List<MethodSpec> methodSpecs = convertDefinitions(methodDefinitions);

        String qualifiedName = klass.getQualifiedName().toString();
        String className = klass.getSimpleName().toString() + "DerrickImpl";
        String classPackage = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));

        TypeSpec classSpec = TypeSpec.classBuilder(className)
                .addSuperinterface(ClassName.get(klass))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethods(methodSpecs)
                .build();

        JavaFile javaFile = JavaFile.builder(classPackage, classSpec).build();
        javaFile.writeTo(filer);

        System.out.println("created " + className + " in " + classPackage);
    }

    private List<MethodSpec> convertDefinitions(List<MethodDefinition> methodDefinitions) {
        return methodDefinitions.stream()
                .map(this::convertDefinition)
                .collect(Collectors.toList());
    }

    private MethodSpec convertDefinition(MethodDefinition md) {
        return MethodSpec.methodBuilder(md.getName())
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC)
                .addCode(md.getCode())
                .build();
    }

}
