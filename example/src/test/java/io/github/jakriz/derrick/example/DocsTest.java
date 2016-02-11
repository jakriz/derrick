package io.github.jakriz.derrick.example;

import io.github.jakriz.derrick.Derrick;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DocsTest {

    private DocsMethods docsMethods = Derrick.get(DocsMethods.class);

    private MathWizard mathWizard = new MathWizard();

    @Test
    public void testAdd() throws Exception {
        assertEquals(docsMethods.add(mathWizard), 6);
    }
}
