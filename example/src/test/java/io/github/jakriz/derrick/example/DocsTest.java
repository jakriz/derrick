package io.github.jakriz.derrick.example;

import io.github.jakriz.derrick.Derrick;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DocsTest {

    private DocsMethods docsMethods = Derrick.get(DocsMethods.class);

    private MathWizard mathWizard = new MathWizard();

    @Test
    public void testMathWizardAdd() throws Exception {
        assertEquals(docsMethods.add(mathWizard), 6);
    }

    @Test
    public void testListAddElement() throws Exception {
        List<String> result = docsMethods.addElement();
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "a");
    }
}
