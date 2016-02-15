package io.github.jakriz.derrick.integration;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.github.jakriz.derrick.annotation.DerrickInterface;
import io.github.jakriz.derrick.annotation.SourceFrom;
import io.github.jakriz.derrick.processor.DerrickProcessor;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.*;

public class DerrickProcessorTest {
    private static final String NL = System.lineSeparator();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Mock
    private ProcessingEnvironment processingEnvironment;

    @Mock
    private Messager messager;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Filer filer;

    @Mock
    private RoundEnvironment roundEnvironment;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TypeElement interfaceElement;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ExecutableElement methodElement;

    private Writer writer;

    private DerrickProcessor victim;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        writer = new StringWriter();

        givenProcessingEnvironmentSetUp();
        victim = new DerrickProcessor();
        victim.init(processingEnvironment);
    }

    private void givenProcessingEnvironmentSetUp() throws Exception {
        when(processingEnvironment.getFiler()).thenReturn(filer);
        when(processingEnvironment.getMessager()).thenReturn(messager);
        when(filer.createSourceFile(any(CharSequence.class)).openWriter()).thenReturn(writer);
    }

    @Test
    public void testProcess_noInterfaceAnnotation() throws Exception {
        givenRoundEnvironmentReturnsNoInterfaceAnnotations();

        victim.process(null, roundEnvironment);

        thenNothingHappens();
    }

    @Test
    public void testProcess_badInterfaceAnnotation() throws Exception {
        givenRoundEnvironmentReturnsInterfaceAnnotationOnClass();

        victim.process(null, roundEnvironment);

        thenErrorHappens();
    }

    @Test
    public void testProcess_noMethodAnnotations() throws Exception {
        givenRoundEnvironmentReturnsInterfaceAnnotation();
        givenInterfaceReturnsNoMethodAnnotations();

        victim.process(null, roundEnvironment);

        thenImplementationClassCreated("EmptyImplementation.java");
    }

    @Test
    public void testProcess_noMethodAnnotations_cannotCreateClass() throws Exception {
        givenRoundEnvironmentReturnsInterfaceAnnotation();
        givenInterfaceReturnsNoMethodAnnotations();
        givenWriterThrowsException();

        victim.process(null, roundEnvironment);

        thenErrorHappens();
    }

    @Test
    public void testProcess_wrongMethodAnnotation() throws Exception {
        givenRoundEnvironmentReturnsInterfaceAnnotation();
        givenInterfaceReturnsWrongMethodAnnotation();

        victim.process(null, roundEnvironment);

        thenImplementationClassCreated("EmptyImplementation.java");
    }

    @Test
    public void testProcess_correctMethodAnnotation_cannotDownload() throws Exception {
        givenRoundEnvironmentReturnsInterfaceAnnotation();
        givenInterfaceReturnsCorrectMethodAnnotation();

        victim.process(null, roundEnvironment);

        verify(getRequestedFor(urlMatching("/sampleTutorial")));
        thenErrorHappens();
    }

    @Test
    public void testProcess_correctMethodAnnotation_downloads() throws Exception {
        givenRoundEnvironmentReturnsInterfaceAnnotation();
        givenInterfaceReturnsCorrectMethodAnnotation();
        givenCodeDownloads("sampleTutorial", "sampleTutorial.html");

        victim.process(null, roundEnvironment);

        verify(getRequestedFor(urlMatching("/sampleTutorial")));
        thenImplementationClassCreated("SampleTutorialImplementation.java");
    }

    @Test
    public void testProcess_correctMethodAnnotation_downloadsWrong() throws Exception {
        givenRoundEnvironmentReturnsInterfaceAnnotation();
        givenInterfaceReturnsCorrectMethodAnnotation();
        givenCodeDownloads("sampleTutorial", "sampleTutorialWrongClass.html");

        victim.process(null, roundEnvironment);

        verify(getRequestedFor(urlMatching("/sampleTutorial")));
        thenErrorHappens();
    }

    private void givenRoundEnvironmentReturnsNoInterfaceAnnotations() {
        when(roundEnvironment.getElementsAnnotatedWith(eq(DerrickInterface.class))).thenReturn(Collections.emptySet());
    }

    private void givenRoundEnvironmentReturnsInterfaceAnnotationOnClass() {
        when(interfaceElement.getKind()).thenReturn(ElementKind.CLASS);
        doReturn(Collections.singleton(interfaceElement)).when(roundEnvironment).getElementsAnnotatedWith(eq(DerrickInterface.class));
    }

    private void givenRoundEnvironmentReturnsInterfaceAnnotation() {
        when(interfaceElement.getKind()).thenReturn(ElementKind.INTERFACE);
        when(interfaceElement.getSimpleName().toString()).thenReturn("TestInterface");
        when(interfaceElement.getQualifiedName().toString()).thenReturn("com.test.TestInterface");
        when(interfaceElement.getAnnotation(eq(DerrickInterface.class))).thenReturn(new DerrickInterfaceLiteral());
        doReturn(Collections.singleton(interfaceElement)).when(roundEnvironment).getElementsAnnotatedWith(eq(DerrickInterface.class));
    }

    private void givenInterfaceReturnsNoMethodAnnotations() {
        when(interfaceElement.getEnclosedElements()).thenReturn(Collections.emptyList());
    }

    private void givenInterfaceReturnsWrongMethodAnnotation() {
        when(methodElement.getKind()).thenReturn(ElementKind.ENUM_CONSTANT);
        doReturn(Collections.singletonList(methodElement)).when(interfaceElement).getEnclosedElements();
    }

    private void givenInterfaceReturnsCorrectMethodAnnotation() {
        when(methodElement.getKind()).thenReturn(ElementKind.METHOD);
        when(methodElement.getSimpleName().toString()).thenReturn("theMethod");
        when(methodElement.getReturnType().toString()).thenReturn("List<String>");
        when(methodElement.getParameters()).thenReturn(Collections.emptyList());
        when(methodElement.getAnnotation(eq(SourceFrom.class))).thenReturn(new SourceFromLiteral());
        doReturn(Collections.singletonList(methodElement)).when(interfaceElement).getEnclosedElements();
    }

    private void givenWriterThrowsException() throws Exception {
        when(filer.createSourceFile(any(CharSequence.class)).openWriter()).thenThrow(new IOException());
    }

    private void givenCodeDownloads(String path, String fileName) throws Exception {
        String fileContent = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("fixtures/"+fileName));
        stubFor(get(urlMatching("/"+path))
                .willReturn(aResponse().withBody(fileContent)));
    }

    private void thenNothingHappens() {
        assertThat(writer.toString()).isNullOrEmpty();
        verifyNoMoreInteractions(messager);
    }

    private void thenErrorHappens() {
        verify(messager).printMessage(eq(Diagnostic.Kind.ERROR), anyString());
        assertThat(writer.toString()).isNullOrEmpty();
    }

    private void thenImplementationClassCreated(String fileName) throws Exception {
        String fileContent = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("results/"+fileName));
        assertThat(writer.toString().replaceAll(NL, "").replaceAll(" ", "")).isEqualTo(fileContent.replaceAll(NL, "").replaceAll(" ", ""));
    }


    private class DerrickInterfaceLiteral implements Annotation, DerrickInterface {
        @Override
        public String baseUrl() {
            return "http://localhost:8089";
        }

        @Override
        public String[] imports() {
            return new String[]{"java.util.*"};
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return DerrickInterface.class;
        }
    }

    private class SourceFromLiteral implements Annotation, SourceFrom {

        @Override
        public String path() {
            return "sampleTutorial";
        }

        @Override
        public String selector() {
            return ".the-code";
        }

        @Override
        public boolean returnLast() {
            return false;
        }

        @Override
        public String addReturn() {
            return "";
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return SourceFrom.class;
        }
    }
}
