package com.jakubkriz.derrick.generator;

import com.jakubkriz.derrick.model.ResolvedInterface;
import com.jakubkriz.derrick.model.ResolvedMethod;

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
    public void generate(ResolvedInterface resolvedInterface, List<ResolvedMethod> resolvedMethods) throws IOException {
        String qualifiedName = resolvedInterface.getGeneratedPackage() + "." + resolvedInterface.getGeneratedSimpleName();
        JavaFileObject fileObject = filer.createSourceFile(qualifiedName);
        Writer writer = fileObject.openWriter();

        writer.append("package ")
                .append(resolvedInterface.getGeneratedPackage())
                .append(";")
                .append(NL)
                .append(NL);

        for (String importStatement : resolvedInterface.getImports()) {
            writer.append("import ")
                    .append(importStatement)
                    .append(";")
                    .append(NL);
        }

        writer.append(NL)
                .append("public class ")
                .append(resolvedInterface.getGeneratedSimpleName())
                .append(" implements ")
                .append(resolvedInterface.getOriginalSimpleName())
                .append(" {")
                .append(NL);

        for (ResolvedMethod method : resolvedMethods) {
            addMethod(writer, method);
        }

        writer.append("}")
                .append(NL);
        writer.flush();
        writer.close();
    }

    private void addMethod(Writer writer, ResolvedMethod method) throws IOException {
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

    private String getAsArgumentsString(List<ResolvedMethod.Argument> arguments) {
        return arguments.stream()
                .map(argument -> argument.getName() + " " + argument.getType())
                .collect(Collectors.joining(", "));
    }

}
