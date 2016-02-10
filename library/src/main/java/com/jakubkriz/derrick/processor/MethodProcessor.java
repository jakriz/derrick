package com.jakubkriz.derrick.processor;

import com.jakubkriz.derrick.annotation.DerrickInterface;
import com.jakubkriz.derrick.annotation.SourceFrom;
import com.jakubkriz.derrick.downloader.CodeDownloader;
import com.jakubkriz.derrick.model.Argument;
import com.jakubkriz.derrick.model.ProcessedMethod;
import com.jakubkriz.derrick.processor.util.CodeModifier;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.*;
import java.util.stream.Collectors;

public class MethodProcessor {

    private CodeDownloader codeDownloader;
    private CodeModifier codeModifier;

    public MethodProcessor(CodeDownloader codeDownloader, CodeModifier codeModifier) {
        this.codeDownloader = codeDownloader;
        this.codeModifier = codeModifier;
    }

    public Optional<ProcessedMethod> process(TypeElement interfaceElement, ExecutableElement methodElement) {
        DerrickInterface interfaceAnnotation = interfaceElement.getAnnotation(DerrickInterface.class);
        SourceFrom methodAnnotation = methodElement.getAnnotation(SourceFrom.class);

        Optional<String> code = codeDownloader.getMethodCode(interfaceAnnotation.baseUrl(), methodAnnotation.path(), methodAnnotation.selector());
        if (code.isPresent()) {
            String modifiedCode = codeModifier.removeTopLevelMethod(code.get());
            if (methodAnnotation.addReturn()) {
                modifiedCode = codeModifier.changeToAddReturnOnLastLine(modifiedCode);
            }

            ProcessedMethod processedMethod = new ProcessedMethod();
            processedMethod.setName(methodElement.getSimpleName().toString());
            processedMethod.setArguments(getArgumentsList(methodElement));
            processedMethod.setReturnType(methodElement.getReturnType().toString());
            processedMethod.setCode(modifiedCode);

            return Optional.of(processedMethod);
        } else {
            return Optional.empty();
        }
    }

    private List<Argument> getArgumentsList(ExecutableElement methodElement) {
        return methodElement.getParameters().stream()
                .map(parameter -> new Argument(parameter.getSimpleName().toString(), parameter.asType().toString()))
                .collect(Collectors.toList());
    }
}
