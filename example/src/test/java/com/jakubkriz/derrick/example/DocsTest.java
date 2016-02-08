package com.jakubkriz.derrick.example;

import com.jakubkriz.derrick.Derrick;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DocsTest {

    private DocsMethods docsMethods = Derrick.get(DocsMethods.class);

    @Test
    public void testAdd() throws Exception {
        assertEquals(docsMethods.add(), 6);
    }
}
