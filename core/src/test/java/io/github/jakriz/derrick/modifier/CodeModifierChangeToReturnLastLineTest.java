package io.github.jakriz.derrick.modifier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class CodeModifierChangeToReturnLastLineTest {
    private static final String NL = System.lineSeparator();

    private CodeModifier victim = new CodeModifier();

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"a();", "return a();"},
                {"a();"+NL+"b();", "a();"+NL+"return b();"},
                {"Transaction transaction = getTransaction();", "return getTransaction();"},
                {"transaction = getTransaction();", "return getTransaction();"},
                {"transaction = myTx;", "return myTx;"},
        });
    }

    @Parameterized.Parameter(value = 0)
    public String original;

    @Parameterized.Parameter(value = 1)
    public String modified;

    @Test
    public void test() throws Exception {
        assertThat(victim.changeToReturnLastLine(original)).isEqualTo(modified);
    }
}