package io.github.jakriz.derrick.modifier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class CodeModifierRemoveTopLevelMethodTest {
    private static final String NL = System.lineSeparator();

    private CodeModifier victim = new CodeModifier();

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"a = b();", "a = b();"},
                {"a = b();"+NL+"String c = a;", "a = b();"+NL+"String c = a;"},
                {"void foo() {"+NL+"a = b();"+NL+"}", "a = b();"},
                {"public void foo() {"+NL+"a = b();"+NL+"}", "a = b();"},
                {"private void foo() {"+NL+"a = b();"+NL+"}", "a = b();"},
                {"protected void foo() {"+NL+"a = b();"+NL+"}", "a = b();"},
                {"public static void foo() {"+NL+"a = b();"+NL+"}", "a = b();"},
                {"List<String> foo() {"+NL+"a = b();"+NL+"}", "a = b();"},
                {"List<String> foo(Map<String, List<Integer>> aMap) {"+NL+"a = b();"+NL+"}", "a = b();"},
                {"List<String> foo(Map<String, List<Integer>> aMap) {"+NL+"List<String> a = b();"+NL+"return a;"+NL+"}", "List<String> a = b();"+NL+"return a;"},
                {"void foo(Map<String, List<Integer>> aMap) {"+NL+"List<String> a = b();"+NL+"c = a;"+NL+"}", "List<String> a = b();"+NL+"c = a;"},
                {NL+"void foo() {"+NL+"a = b();"+NL+"}", "a = b();"},
                {NL+NL+"void foo() {"+NL+"a = b();"+NL+"}"+NL, "a = b();"},
                {"void foo() {a = b();}", "a = b();"},
                {"void foo(Map<String, List<Integer>> aMap) {a = b(); c = d; return c;}", "a = b(); c = d; return c;"},
                {"void foo() {"+NL+"if (2+2==4) {"+NL+"a = b();"+NL+"}"+NL+"}", "if (2+2==4) {"+NL+"a = b();"+NL+"}"},
                {"void foo() {"+NL+"if (2+2==4) {a = b();}"+NL+"}", "if (2+2==4) {a = b();}"},
        });
    }

    @Parameterized.Parameter(value = 0)
    public String original;

    @Parameterized.Parameter(value = 1)
    public String expected;

    @Test
    public void test() throws Exception {
        assertThat(victim.removeTopLevelMethod(original).replaceAll(NL, "")).isEqualTo(expected.replaceAll(NL, ""));
    }

}