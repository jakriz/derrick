package io.github.jakriz.derrick.downloader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class UrlHelperTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {null, null, null},
                {null, "", null},
                {null, "a", null},
                {"http://www.example.com", null, "http://www.example.com/"},
                {"http://www.example.com", "", "http://www.example.com/"},
                {"http://www.example.com/", null, "http://www.example.com/"},
                {"http://www.example.com/", "", "http://www.example.com/"},
                {"http://www.example.com/", "a", "http://www.example.com/a"},
                {"http://www.example.com/", "a/", "http://www.example.com/a/"},
                {"http://www.example.com/", "/a", "http://www.example.com/a"},
                {"http://www.example.com/", "/a/", "http://www.example.com/a/"},
                {"http://www.example.com", "a", "http://www.example.com/a"},
                {"http://www.example.com", "/a", "http://www.example.com/a"},
                {"http://www.example.com", "/a/", "http://www.example.com/a/"},
        });
    }

    @Parameterized.Parameter(value = 0)
    public String url;

    @Parameterized.Parameter(value = 1)
    public String path;

    @Parameterized.Parameter(value = 2)
    public String expectedResult;

    @Test
    public void testMakeFromUrlAndPath() throws Exception {
        if (expectedResult == null) {
            expectedException.expect(IllegalArgumentException.class);
        } else {
            expectedException = ExpectedException.none();
        }

        assertThat(UrlHelper.makeFromUrlAndPath(url, path)).isEqualTo(expectedResult);
    }
}
