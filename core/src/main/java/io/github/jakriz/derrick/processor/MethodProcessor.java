package io.github.jakriz.derrick.processor;

import io.github.jakriz.derrick.annotation.DerrickInterface;
import io.github.jakriz.derrick.annotation.SourceFrom;
import io.github.jakriz.derrick.downloader.CodeDownloader;
import io.github.jakriz.derrick.model.Argument;
import io.github.jakriz.derrick.model.ProcessedMethod;
import io.github.jakriz.derrick.processor.util.CodeModifier;

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
                .map(parameter -> new Argument(parameter.asType().toString(), parameter.getSimpleName().toString()))
                .collect(Collectors.toList());
    }
}
