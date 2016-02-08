package com.jakubkriz.derrick.processor;

import com.jakubkriz.derrick.annotation.DerrickInterface;
import com.jakubkriz.derrick.annotation.SourceFrom;
import com.jakubkriz.derrick.downloader.CodeDownloader;
import com.jakubkriz.derrick.model.ResolvedMethod;
import com.jakubkriz.derrick.processor.util.CodeModifier;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.*;

public class MethodProcessor {

    private CodeDownloader codeDownloader;
    private CodeModifier codeModifier;

    public MethodProcessor(CodeDownloader codeDownloader, CodeModifier codeModifier) {
        this.codeDownloader = codeDownloader;
        this.codeModifier = codeModifier;
    }

    public ResolvedMethod process(TypeElement interfaceElement, ExecutableElement methodElement) {
        DerrickInterface interfaceAnnotation = interfaceElement.getAnnotation(DerrickInterface.class);
        SourceFrom methodAnnotation = methodElement.getAnnotation(SourceFrom.class);

        Optional<String> code = codeDownloader.getMethodCode(interfaceAnnotation.baseUrl(), methodAnnotation.path(), methodAnnotation.selector());
        String modifiedCode = codeModifier.removeTopLevelMethod(code.get());
        if (methodAnnotation.addReturn()) {
            modifiedCode = codeModifier.changeToAddReturnOnLastLine(modifiedCode);
        }

        ResolvedMethod resolvedMethod = new ResolvedMethod();
        resolvedMethod.setName(methodElement.getSimpleName().toString());
        resolvedMethod.setArguments(Collections.emptyList());
        resolvedMethod.setReturnType("int");
        resolvedMethod.setCode(modifiedCode);

        return resolvedMethod;
    }
}
