package io.github.jakriz.derrick.modifier;

import io.github.jakriz.derrick.modifier.CodeModifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class CodeModifierChangeToReturnSpecifiedTest {
    private static final String NL = System.lineSeparator();

    private CodeModifier victim = new CodeModifier();

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"", "a", "return a;"},
                {"a = b();", "a", "return a;"},
                {"a = b();"+NL+"b = c();", "b", "return b;"},
                {"Intent intent = ui.createTransactionSummaryIntent(\"transactionIdentifier\");"+NL+"startActivityForResult(intent, MposUi.REQUEST_CODE_SHOW_SUMMARY);", "intent", "return intent;"}
        });
    }

    @Parameterized.Parameter(value = 0)
    public String original;

    @Parameterized.Parameter(value = 1)
    public String toReturn;

    @Parameterized.Parameter(value = 2)
    public String newLastLine;

    @Test
    public void test() throws Exception {
        String result = victim.changeToReturnSpecified(original, toReturn);

        thenFirstPartOfCodeUnmodified(result);
        thenLastLineWithReturnAdded(result);
    }

    private void thenFirstPartOfCodeUnmodified(String result) {
        assertThat(result.substring(0, result.lastIndexOf(NL))).isEqualTo(original);
    }

    private void thenLastLineWithReturnAdded(String result) {
        assertThat(result.substring(result.lastIndexOf(NL)+1)).isEqualTo(newLastLine);
    }
}