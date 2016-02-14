package io.github.jakriz.derrick.generator;

import io.github.jakriz.derrick.model.Argument;
import io.github.jakriz.derrick.model.ProcessedInterface;
import io.github.jakriz.derrick.model.ProcessedMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class HardcoreClassGeneratorTest {
    private static final String NL = System.lineSeparator();

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Filer filer;

    private Writer writer;

    private HardcoreClassGenerator victim;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        victim = new HardcoreClassGenerator(filer);
        writer = new StringWriter();
    }

    @Test
    public void testGenerate() throws Exception {
        ProcessedInterface processedInterface = makeProcessedInterface();
        List<ProcessedMethod> processedMethods = makeProcessedMethods();
        givenFilerCreatesAndReturnsOurWriter();

        victim.generate(processedInterface, processedMethods);

        assertThat(writer.toString().replaceAll(NL, "")).isEqualToIgnoringCase(makeGeneratedClass().replaceAll(NL, ""));
    }

    private void givenFilerCreatesAndReturnsOurWriter() throws IOException {
        when(filer.createSourceFile(eq("com.test.MyClassDerrickImpl")).openWriter()).thenReturn(writer);
    }

    private List<ProcessedMethod> makeProcessedMethods() {
        ProcessedMethod processedMethod = new ProcessedMethod();
        processedMethod.setName("foo");
        processedMethod.setArguments(Collections.singletonList(new Argument("String", "a")));
        processedMethod.setReturnType("List<String>");
        processedMethod.setCode("return Collections.singletonList(a);");
        return Collections.singletonList(processedMethod);
    }

    private ProcessedInterface makeProcessedInterface() {
        ProcessedInterface processedInterface = new ProcessedInterface();
        processedInterface.setGeneratedPackage("com.test");
        processedInterface.setGeneratedSimpleName("MyClassDerrickImpl");
        processedInterface.setOriginalSimpleName("MyClass");
        processedInterface.setImports(Collections.singleton("java.util.*"));
        return processedInterface;
    }

    private String makeGeneratedClass() {
        return "package com.test;" +
                "import java.util.*;" +
                "public class MyClassDerrickImpl implements MyClass {" +
                "public List<String> foo(String a) {" +
                "return Collections.singletonList(a);" +
                "}" +
                "}";
    }
}
