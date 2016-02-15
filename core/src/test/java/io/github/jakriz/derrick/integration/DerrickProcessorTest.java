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
import javax.lang.model.element.VariableElement;
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

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private VariableElement methodArgument;

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
        givenInterfaceReturnsCorrectMethodAnnotation(new SourceFromBasicLiteral());

        victim.process(null, roundEnvironment);

        verify(getRequestedFor(urlMatching("/tutorialBasic")));
        thenErrorHappens();
    }

    @Test
    public void testProcess_correctMethodAnnotation_downloadTimesOut() throws Exception {
        givenRoundEnvironmentReturnsInterfaceAnnotation();
        givenInterfaceReturnsCorrectMethodAnnotation(new SourceFromBasicLiteral());
        givenDownloadTimesOut("tutorialBasic");

        victim.process(null, roundEnvironment);

        verify(getRequestedFor(urlMatching("/tutorialBasic")));
        thenErrorHappens();
    }

    @Test
    public void testProcess_correctMethodAnnotation_downloads_basic() throws Exception {
        givenRoundEnvironmentReturnsInterfaceAnnotation();
        givenInterfaceReturnsCorrectMethodAnnotation(new SourceFromBasicLiteral());
        givenCodeDownloads("tutorialBasic", "tutorialBasic.html");

        victim.process(null, roundEnvironment);

        verify(getRequestedFor(urlMatching("/tutorialBasic")));
        thenImplementationClassCreated("TutorialBasicImplementation.java");
    }

    @Test
    public void testProcess_correctMethodAnnotation_downloads_wrongClass() throws Exception {
        givenRoundEnvironmentReturnsInterfaceAnnotation();
        givenInterfaceReturnsCorrectMethodAnnotation(new SourceFromBasicLiteral());
        givenCodeDownloads("tutorialBasic", "tutorialWrongClass.html");

        victim.process(null, roundEnvironment);

        verify(getRequestedFor(urlMatching("/tutorialBasic")));
        thenErrorHappens();
    }

    @Test
    public void testProcess_correctMethodAnnotation_downloads_oneLiner() throws Exception {
        givenRoundEnvironmentReturnsInterfaceAnnotation();
        givenInterfaceReturnsCorrectMethodAnnotation(new SourceFromOneLinerLiteral());
        givenCodeDownloads("tutorialOneLiner", "tutorialOneLiner.html");

        victim.process(null, roundEnvironment);

        verify(getRequestedFor(urlMatching("/tutorialOneLiner")));
        thenImplementationClassCreated("TutorialOneLinerImplementation.java");
    }

    @Test
    public void testProcess_correctMethodAnnotation_downloads_complicated() throws Exception {
        givenRoundEnvironmentReturnsInterfaceAnnotation();
        givenInterfaceReturnsCorrectMethodAnnotation(new SourceFromComplicatedLiteral());
        givenInterfaceMethodHasArguments();
        givenCodeDownloads("tutorialComplicated", "tutorialComplicated.html");

        victim.process(null, roundEnvironment);

        verify(getRequestedFor(urlMatching("/tutorialComplicated")));
        thenImplementationClassCreated("TutorialComplicatedImplementation.java");
    }


    private void givenProcessingEnvironmentSetUp() throws Exception {
        when(processingEnvironment.getFiler()).thenReturn(filer);
        when(processingEnvironment.getMessager()).thenReturn(messager);
        when(filer.createSourceFile(any(CharSequence.class)).openWriter()).thenReturn(writer);
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

    private void givenInterfaceReturnsCorrectMethodAnnotation(SourceFrom sourceFrom) {
        when(methodElement.getKind()).thenReturn(ElementKind.METHOD);
        when(methodElement.getSimpleName().toString()).thenReturn("theMethod");
        when(methodElement.getReturnType().toString()).thenReturn("List<String>");
        when(methodElement.getParameters()).thenReturn(Collections.emptyList());
        when(methodElement.getAnnotation(eq(SourceFrom.class))).thenReturn(sourceFrom);
        doReturn(Collections.singletonList(methodElement)).when(interfaceElement).getEnclosedElements();
    }

    private void givenInterfaceMethodHasArguments() {
        when(methodArgument.asType().toString()).thenReturn("List<String>");
        when(methodArgument.getSimpleName().toString()).thenReturn("list");
        doReturn(Collections.singletonList(methodArgument)).when(methodElement).getParameters();
    }

    private void givenWriterThrowsException() throws Exception {
        when(filer.createSourceFile(any(CharSequence.class)).openWriter()).thenThrow(new IOException());
    }

    private void givenDownloadTimesOut(String path) {
        stubFor(get(urlMatching("/"+path))
                .willReturn(aResponse().withStatus(200).withFixedDelay(11000)));
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

    private abstract class SourceFromLiteral implements Annotation, SourceFrom {
        @Override
        public String selector() {
            return ".the-code";
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return SourceFrom.class;
        }
    }

    private class SourceFromBasicLiteral extends SourceFromLiteral {
        @Override
        public String path() {
            return "tutorialBasic";
        }

        @Override
        public boolean returnLast() {
            return false;
        }

        @Override
        public String addReturn() {
            return "";
        }
    }

    private class SourceFromOneLinerLiteral extends SourceFromLiteral {
        @Override
        public String path() {
            return "tutorialOneLiner";
        }

        @Override
        public boolean returnLast() {
            return true;
        }

        @Override
        public String addReturn() {
            return "";
        }
    }

    private class SourceFromComplicatedLiteral extends SourceFromLiteral {
        @Override
        public String path() {
            return "tutorialComplicated";
        }

        @Override
        public boolean returnLast() {
            return false;
        }

        @Override
        public String addReturn() {
            return "list";
        }
    }
}
