package com.jakubkriz.derrick.processor;

import com.jakubkriz.derrick.annotation.DerrickInterface;
import com.jakubkriz.derrick.annotation.SourceFrom;
import com.jakubkriz.derrick.downloader.CodeDownloader;
import com.jakubkriz.derrick.downloader.HttpClient;

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
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({
        "com.jakubkriz.derrick.annotation.DerrickInterface",
        "com.jakubkriz.derrick.annotation.SourceFrom"
})
public class DerrickProcessor extends AbstractProcessor {

    private CodeDownloader codeDownloader = new CodeDownloader(new HttpClient());
    private MethodProcessor methodProcessor = new MethodProcessor(codeDownloader);
    private ClassGenerator classGenerator = new ClassGenerator();

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
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element classElement : roundEnv.getElementsAnnotatedWith(DerrickInterface.class)) {
            if (classElement.getKind() != ElementKind.INTERFACE) {
                error("Only interfaces can be annotated with @DerrickInterface annotation.");
                return false;
            }

            List<MethodDefinition> methods = classElement.getEnclosedElements()
                    .stream()
                    .filter(e -> e.getKind() == ElementKind.METHOD)
                    .filter(e -> e.getAnnotation(SourceFrom.class) != null)
                    .map(e -> (ExecutableElement) e)
                    .map(e -> {
                        try {
                            return methodProcessor.process(e);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            return null;
                        }
                    })
                    .collect(Collectors.toList());

            try {
                classGenerator.generate((TypeElement) classElement, methods, filer);
            } catch (IOException e) {
                e.printStackTrace();
                return true;
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
}
