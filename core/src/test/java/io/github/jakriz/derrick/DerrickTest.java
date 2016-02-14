package io.github.jakriz.derrick;

import io.github.jakriz.derrick.fixtures.NotProcessedTestInterface;
import io.github.jakriz.derrick.fixtures.TestInterface;
import io.github.jakriz.derrick.fixtures.TestInterfaceDerrickImpl;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DerrickTest {

    @Test(expected = RuntimeException.class)
    public void testGet_nonExistent() throws Exception {
        Derrick.get(NotProcessedTestInterface.class);
    }

    @Test
    public void testGet_exists() throws Exception {
        TestInterface result = Derrick.get(TestInterface.class);
        assertThat(result).isExactlyInstanceOf(TestInterfaceDerrickImpl.class);
    }
}
