package io.github.jakriz.derrick.processor;

import io.github.jakriz.derrick.processor.Namer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.lang.model.element.TypeElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class NamerTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TypeElement typeElement;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(typeElement.getSimpleName().toString()).thenReturn("MyClass");
        when(typeElement.getQualifiedName().toString()).thenReturn("com.company.my.MyClass");
    }

    @Test
    public void testGeneratedClassName() throws Exception {
        assertThat(Namer.generatedClassName(typeElement)).isEqualTo("MyClassDerrickImpl");
    }

    @Test
    public void testGeneratedPackageName() throws Exception {
        assertThat(Namer.generatedPackageName(typeElement)).isEqualTo("com.company.my");
    }

    @Test
    public void testGeneratedQualifiedName_String() throws Exception {
        assertThat(Namer.generatedQualifiedName(typeElement)).isEqualTo("com.company.my.MyClassDerrickImpl");
    }

    @Test
    public void testGeneratedQualifiedName_Class() throws Exception {
        assertThat(Namer.generatedQualifiedName(this.getClass())).isEqualTo("io.github.jakriz.derrick.processor.NamerTestDerrickImpl");
    }
}
