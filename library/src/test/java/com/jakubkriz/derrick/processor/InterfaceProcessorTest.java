package com.jakubkriz.derrick.processor;

import com.jakubkriz.derrick.annotation.DerrickInterface;
import com.jakubkriz.derrick.model.ProcessedInterface;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.lang.model.element.TypeElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class InterfaceProcessorTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    TypeElement interfaceElement;

    @Mock
    DerrickInterface derrickInterface;

    private InterfaceProcessor victim;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        victim = new InterfaceProcessor();
    }

    @Test
    public void testProcess() throws Exception {
        when(interfaceElement.getAnnotation(eq(DerrickInterface.class))).thenReturn(derrickInterface);
        when(interfaceElement.getSimpleName().toString()).thenReturn("Foo");
        when(interfaceElement.getQualifiedName().toString()).thenReturn("com.a.b.Foo");
        when(derrickInterface.imports()).thenReturn(new String[]{"a", "b"});

        ProcessedInterface result = victim.process(interfaceElement);

        assertThat(result.getOriginalSimpleName()).isEqualTo("Foo");
        assertThat(result.getGeneratedSimpleName()).isEqualTo("FooDerrickImpl");
        assertThat(result.getGeneratedPackage()).isEqualTo("com.a.b");
        assertThat(result.getImports()).containsExactly("a", "b");
    }
}
