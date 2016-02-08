package com.jakubkriz.derrick.processor;

import com.jakubkriz.derrick.annotation.DerrickInterface;
import com.jakubkriz.derrick.annotation.SourceFrom;
import com.jakubkriz.derrick.downloader.CodeDownloader;
import com.jakubkriz.derrick.model.ResolvedMethod;
import com.jakubkriz.derrick.processor.util.CodeModifier;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        victim = new MethodProcessor(codeDownloader, codeModifier);
    }

    @Test
    public void testProcess_addReturnTrue() throws Exception {
        givenElementsHaveAnnotations();
        givenElementsReturnValues();
        givenAnnotationsReturnValues(true);
        givenCodeDownloaderReturnsCode();
        givenCodeModifierReturnsCode();

        ResolvedMethod result = victim.process(interfaceElement, methodElement);

        thenCorrectCodeModifiersAreCalled(true);
        thenCorrectResolvedMethodIsReturned(result);
    }

    @Test
    public void testProcess_addReturnFalse() throws Exception {
        givenElementsHaveAnnotations();
        givenElementsReturnValues();
        givenAnnotationsReturnValues(false);
        givenCodeDownloaderReturnsCode();
        givenCodeModifierReturnsCode();

        ResolvedMethod result = victim.process(interfaceElement, methodElement);

        thenCorrectCodeModifiersAreCalled(false);
        thenCorrectResolvedMethodIsReturned(result);
    }

    private void givenElementsHaveAnnotations() {
        when(interfaceElement.getAnnotation(eq(DerrickInterface.class))).thenReturn(derrickInterface);
        when(methodElement.getAnnotation(eq(SourceFrom.class))).thenReturn(sourceFrom);
    }

    private void givenElementsReturnValues() {
        when(methodElement.getSimpleName().toString()).thenReturn("foo()");
    }

    private void givenAnnotationsReturnValues(boolean addReturn) {
        when(derrickInterface.baseUrl()).thenReturn("http://www.example.com");
        when(sourceFrom.path()).thenReturn("abc");
        when(sourceFrom.selector()).thenReturn(".xyz");
        when(sourceFrom.addReturn()).thenReturn(addReturn);
    }

    private void givenCodeDownloaderReturnsCode() {
        when(codeDownloader.getMethodCode(eq("http://www.example.com"), eq("abc"), eq(".xyz"))).thenReturn(of(CODE_SAMPLE));
    }

    private void givenCodeModifierReturnsCode() {
        when(codeModifier.removeTopLevelMethod(anyString())).thenReturn(CODE_SAMPLE);
        when(codeModifier.changeToAddReturnOnLastLine(anyString())).thenReturn(CODE_SAMPLE);
    }

    private void thenCorrectCodeModifiersAreCalled(boolean addReturn) {
        verify(codeModifier).removeTopLevelMethod(eq(CODE_SAMPLE));
        if (addReturn) {
            verify(codeModifier).changeToAddReturnOnLastLine(eq(CODE_SAMPLE));
        } else {
            verify(codeModifier, never()).changeToAddReturnOnLastLine(anyString());
        }
    }

    private void thenCorrectResolvedMethodIsReturned(ResolvedMethod result) {
        assertThat(result.getName()).isEqualTo("foo()");
        assertThat(result.getCode()).isEqualTo(CODE_SAMPLE);
    }
}
