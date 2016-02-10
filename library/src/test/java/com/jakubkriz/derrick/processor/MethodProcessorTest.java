package com.jakubkriz.derrick.processor;

import com.jakubkriz.derrick.annotation.DerrickInterface;
import com.jakubkriz.derrick.annotation.SourceFrom;
import com.jakubkriz.derrick.downloader.CodeDownloader;
import com.jakubkriz.derrick.model.ProcessedMethod;
import com.jakubkriz.derrick.processor.util.CodeModifier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class MethodProcessorTest {
    private static final String CODE_SAMPLE = "someCode();";

    @Mock
    private CodeDownloader codeDownloader;

    @Mock
    private CodeModifier codeModifier;

    @Mock
    private TypeElement interfaceElement;

    @Mock
    private DerrickInterface derrickInterface;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ExecutableElement methodElement;

    @Mock
    private SourceFrom sourceFrom;

    private MethodProcessor victim;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true}, {false}
        });
    }

    @Parameterized.Parameter
    public boolean addReturn;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        victim = new MethodProcessor(codeDownloader, codeModifier);
    }

    @Test
    public void testProcess_noCodeReturned() throws Exception {
        givenElementsHaveAnnotations();
        givenElementsReturnValues();
        givenAnnotationsReturnValues(addReturn);
        givenCodeDownloaderDoesNotReturnCode();

        Optional<ProcessedMethod> result = victim.process(interfaceElement, methodElement);

        thenEmptyResultIsReturned(result);
    }

    @Test
    public void testProcess_codeReturned() throws Exception {
        givenElementsHaveAnnotations();
        givenElementsReturnValues();
        givenAnnotationsReturnValues(addReturn);
        givenCodeDownloaderReturnsCode();
        givenCodeModifierReturnsCode();

        Optional<ProcessedMethod> result = victim.process(interfaceElement, methodElement);

        thenCorrectCodeModifiersAreCalled(addReturn);
        thenResolvedMethodHasCorrectData(result.get());
    }

    private void givenElementsHaveAnnotations() {
        when(interfaceElement.getAnnotation(eq(DerrickInterface.class))).thenReturn(derrickInterface);
        when(methodElement.getAnnotation(eq(SourceFrom.class))).thenReturn(sourceFrom);
    }

    private void givenElementsReturnValues() {
        when(methodElement.getSimpleName().toString()).thenReturn("foo");
        when(methodElement.getReturnType().toString()).thenReturn("String");

        when(methodElement.getParameters()).thenReturn(Collections.emptyList());
    }

    private void givenAnnotationsReturnValues(boolean addReturn) {
        when(derrickInterface.baseUrl()).thenReturn("http://www.example.com");
        when(sourceFrom.path()).thenReturn("abc");
        when(sourceFrom.selector()).thenReturn(".xyz");
        when(sourceFrom.addReturn()).thenReturn(addReturn);
    }

    private void givenCodeDownloaderDoesNotReturnCode() {
        when(codeDownloader.getMethodCode(eq("http://www.example.com"), eq("abc"), eq(".xyz"))).thenReturn(empty());
    }

    private void givenCodeDownloaderReturnsCode() {
        when(codeDownloader.getMethodCode(eq("http://www.example.com"), eq("abc"), eq(".xyz"))).thenReturn(of(CODE_SAMPLE));
    }

    private void givenCodeModifierReturnsCode() {
        when(codeModifier.removeTopLevelMethod(anyString())).thenReturn(CODE_SAMPLE);
        when(codeModifier.changeToAddReturnOnLastLine(anyString())).thenReturn(CODE_SAMPLE);
    }

    private void thenEmptyResultIsReturned(Optional<ProcessedMethod> result) {
        assertThat(result.isPresent()).isFalse();
    }

    private void thenCorrectCodeModifiersAreCalled(boolean addReturn) {
        verify(codeModifier).removeTopLevelMethod(eq(CODE_SAMPLE));
        if (addReturn) {
            verify(codeModifier).changeToAddReturnOnLastLine(eq(CODE_SAMPLE));
        } else {
            verify(codeModifier, never()).changeToAddReturnOnLastLine(anyString());
        }
    }

    private void thenResolvedMethodHasCorrectData(ProcessedMethod result) {
        assertThat(result.getName()).isEqualTo("foo");
        assertThat(result.getReturnType()).isEqualTo("String");
        assertThat(result.getArguments()).isEmpty();
        assertThat(result.getCode()).isEqualTo(CODE_SAMPLE);
    }
}
