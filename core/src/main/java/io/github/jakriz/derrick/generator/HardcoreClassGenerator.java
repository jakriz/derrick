package io.github.jakriz.derrick.generator;

import io.github.jakriz.derrick.model.Argument;
import io.github.jakriz.derrick.model.ProcessedInterface;
import io.github.jakriz.derrick.model.ProcessedMethod;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

public class HardcoreClassGenerator implements ClassGenerator {
    private static final String NL = System.lineSeparator();

    private Filer filer;

    public HardcoreClassGenerator(Filer filer) {
        this.filer = filer;
    }

    @Override
    public void generate(ProcessedInterface processedInterface, List<ProcessedMethod> processedMethods) throws IOException {
        String qualifiedName = processedInterface.getGeneratedPackage() + "." + processedInterface.getGeneratedSimpleName();
        JavaFileObject fileObject = filer.createSourceFile(qualifiedName);
        Writer writer = fileObject.openWriter();

        writer.append("package ")
                .append(processedInterface.getGeneratedPackage())
                .append(";")
                .append(NL)
                .append(NL);

        for (String importStatement : processedInterface.getImports()) {
            writer.append("import ")
                    .append(importStatement)
                    .append(";")
                    .append(NL);
        }

        writer.append(NL)
                .append("public class ")
                .append(processedInterface.getGeneratedSimpleName())
                .append(" implements ")
                .append(processedInterface.getOriginalSimpleName())
                .append(" {")
                .append(NL);

        for (ProcessedMethod method : processedMethods) {
            addMethod(writer, method);
        }

        writer.append("}")
                .append(NL);
        writer.flush();
        writer.close();
    }

    private void addMethod(Writer writer, ProcessedMethod method) throws IOException {
        writer.append("public ")
                .append(method.getReturnType())
                .append(" ")
                .append(method.getName())
                .append("(")
                .append(getAsArgumentsString(method.getArguments()))
                .append(")")
                .append(" {")
                .append(NL)
                .append(method.getCode())
                .append("}")
                .append(NL)
                .append(NL);
    }

    private String getAsArgumentsString(List<Argument> arguments) {
        return arguments.stream()
                .map(argument -> String.join(" ", argument.getType(), argument.getName()))
                .collect(Collectors.joining(", "));
    }

}
