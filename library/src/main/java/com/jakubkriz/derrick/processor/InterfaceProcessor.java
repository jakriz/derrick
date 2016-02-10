package com.jakubkriz.derrick.processor;

import com.jakubkriz.derrick.annotation.DerrickInterface;
import com.jakubkriz.derrick.model.ProcessedInterface;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class InterfaceProcessor {

    public ProcessedInterface process(TypeElement interfaceElement) {
        DerrickInterface interfaceAnnotation = interfaceElement.getAnnotation(DerrickInterface.class);

        Set<String> imports = new HashSet<>(Arrays.asList(interfaceAnnotation.imports()));

        ProcessedInterface processedInterface = new ProcessedInterface();
        processedInterface.setOriginalSimpleName(interfaceElement.getSimpleName().toString());
        processedInterface.setGeneratedPackage(Namer.generatedPackageName(interfaceElement));
        processedInterface.setGeneratedSimpleName(Namer.generatedClassName(interfaceElement));
        processedInterface.setImports(imports);
        return processedInterface;
    }
}
