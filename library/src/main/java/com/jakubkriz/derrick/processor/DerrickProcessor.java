package com.jakubkriz.derrick.processor;

import com.jakubkriz.derrick.annotation.DerrickInterface;
import com.jakubkriz.derrick.annotation.SourceFrom;
import com.jakubkriz.derrick.downloader.CodeDownloader;
import com.jakubkriz.derrick.downloader.JsoupSelectorExtractor;
import com.jakubkriz.derrick.downloader.OkhttpHttpClient;
import com.jakubkriz.derrick.generator.ClassGenerator;
import com.jakubkriz.derrick.generator.HardcoreClassGenerator;
import com.jakubkriz.derrick.model.ProcessedInterface;
import com.jakubkriz.derrick.model.ProcessedMethod;
import com.jakubkriz.derrick.processor.util.CodeModifier;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SupportedAnnotationTypes({
        "com.jakubkriz.derrick.annotation.DerrickInterface",
        "com.jakubkriz.derrick.annotation.SourceFrom"
})
public class DerrickProcessor extends AbstractProcessor {

    private CodeDownloader codeDownloader = new CodeDownloader(new OkhttpHttpClient(), new JsoupSelectorExtractor());
    private CodeModifier codeModifier = new CodeModifier();
    private InterfaceProcessor interfaceProcessor = new InterfaceProcessor();
    private MethodProcessor methodProcessor = new MethodProcessor(codeDownloader, codeModifier);
    private ClassGenerator classGenerator;

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();

        classGenerator = new HardcoreClassGenerator(filer);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element candidateElement : roundEnv.getElementsAnnotatedWith(DerrickInterface.class)) {
            if (candidateElement.getKind() != ElementKind.INTERFACE) {
                error("Only interfaces can be annotated with @DerrickInterface annotation.");
                return false;
            }
            TypeElement interfaceElement = (TypeElement) candidateElement;

            ProcessedInterface processedInterface = interfaceProcessor.process(interfaceElement);

            List<Optional<ProcessedMethod>> methodsOptionals = interfaceElement.getEnclosedElements()
                    .stream()
                    .filter(e -> e.getKind() == ElementKind.METHOD)
                    .filter(e -> e.getAnnotation(SourceFrom.class) != null)
                    .map(e -> (ExecutableElement) e)
                    .map(e -> methodProcessor.process(interfaceElement, e))
                    .collect(Collectors.toList());

            if (methodsOptionals.stream().allMatch(Optional::isPresent)) {
                List<ProcessedMethod> methods = methodsOptionals.stream()
                        .map(Optional::get)
                        .collect(Collectors.toList());
                try {
                    classGenerator.generate(processedInterface, methods);
                } catch (IOException e) {
                    error("Implementation file couldn't be generated due to: " + e.getMessage());
                    return true;
                }
            } else {
                error("Some methods could not be processed"); // TODO better error messages with reasons and stuff
            }
        }

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void error(String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }

    private void warning(String message) {
        messager.printMessage(Diagnostic.Kind.WARNING, message);
    }
}
