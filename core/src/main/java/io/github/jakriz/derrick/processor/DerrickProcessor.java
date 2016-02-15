package io.github.jakriz.derrick.processor;

import io.github.jakriz.derrick.annotation.DerrickInterface;
import io.github.jakriz.derrick.annotation.SourceFrom;
import io.github.jakriz.derrick.downloader.CodeDownloader;
import io.github.jakriz.derrick.downloader.JsoupSelectorExtractor;
import io.github.jakriz.derrick.downloader.OkhttpHttpClient;
import io.github.jakriz.derrick.generator.ClassGenerator;
import io.github.jakriz.derrick.generator.HardcoreClassGenerator;
import io.github.jakriz.derrick.model.ProcessedInterface;
import io.github.jakriz.derrick.model.ProcessedMethod;
import io.github.jakriz.derrick.modifier.CodeModifier;

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

@SupportedAnnotationTypes({
        "io.github.jakriz.derrick.annotation.DerrickInterface",
        "io.github.jakriz.derrick.annotation.SourceFrom"
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
        return SourceVersion.RELEASE_7;
    }

    private void error(String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }

}
