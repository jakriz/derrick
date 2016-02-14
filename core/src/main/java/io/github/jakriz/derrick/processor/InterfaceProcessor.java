package io.github.jakriz.derrick.processor;

import io.github.jakriz.derrick.annotation.DerrickInterface;
import io.github.jakriz.derrick.model.ProcessedInterface;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class InterfaceProcessor {

    public ProcessedInterface process(TypeElement interfaceElement) {
        DerrickInterface interfaceAnnotation = interfaceElement.getAnnotation(DerrickInterface.class);

        ProcessedInterface processedInterface = new ProcessedInterface();
        processedInterface.setOriginalSimpleName(interfaceElement.getSimpleName().toString());
        processedInterface.setGeneratedPackage(Namer.generatedPackageName(interfaceElement));
        processedInterface.setGeneratedSimpleName(Namer.generatedClassName(interfaceElement));
        processedInterface.setImports(new HashSet<>(Arrays.asList(interfaceAnnotation.imports())));

        return processedInterface;
    }
}
