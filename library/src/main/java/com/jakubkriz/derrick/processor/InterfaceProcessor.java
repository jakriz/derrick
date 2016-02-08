package com.jakubkriz.derrick.processor;

import com.jakubkriz.derrick.annotation.DerrickInterface;
import com.jakubkriz.derrick.model.ResolvedInterface;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class InterfaceProcessor {

    public ResolvedInterface process(TypeElement interfaceElement) {
        DerrickInterface interfaceAnnotation = interfaceElement.getAnnotation(DerrickInterface.class);

        Set<String> imports = new HashSet<>(Arrays.asList(interfaceAnnotation.imports()));

        ResolvedInterface resolvedInterface = new ResolvedInterface();
        resolvedInterface.setOriginalSimpleName(interfaceElement.getSimpleName().toString());
        resolvedInterface.setGeneratedPackage(Namer.generatedPackageName(interfaceElement));
        resolvedInterface.setGeneratedSimpleName(Namer.generatedClassName(interfaceElement));
        resolvedInterface.setImports(imports);
        return resolvedInterface;
    }
}
