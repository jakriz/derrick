package io.github.jakriz.derrick.processor;

import org.junit.Test;

import javax.lang.model.SourceVersion;

import static org.assertj.core.api.Assertions.assertThat;

public class DerrickProcessorTest {

    private DerrickProcessor victim = new DerrickProcessor();

    @Test
    public void testGetSupportedSourceVersion() throws Exception {
        assertThat(victim.getSupportedSourceVersion()).isEqualTo(SourceVersion.RELEASE_7);
    }
}