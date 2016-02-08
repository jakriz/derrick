package com.jakubkriz.derrick.downloader;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class CodeDownloaderTest {

    @Mock
    HttpClient httpClient;

    @Mock
    JsoupSelectorExtractor selectorExtractor;

    private CodeDownloader victim;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        victim = new CodeDownloader(httpClient, selectorExtractor);
    }

    @Test
    public void testGetMethodCode_simpleCode() throws Exception {
        givenHttpClientAndExtractorReturn(of("someCode"));
        Optional<String> result = victim.getMethodCode("http://www.example.com", "a", ".someClass");
        assertThat(result.get()).isEqualTo("someCode");
    }

    @Test
    public void testGetMethodCode_codeWithHtmlEntities() throws Exception {
        givenHttpClientAndExtractorReturn(of("someCode(&quot;ha&quot;)"));
        Optional<String> result = victim.getMethodCode("http://www.example.com", "a", ".someClass");
        assertThat(result.get()).isEqualTo("someCode(\"ha\")");
    }

    @Test
    public void testGetMethodCode_noCode() throws Exception {
        givenHttpClientAndExtractorReturn(empty());
        Optional<String> result = victim.getMethodCode("http://www.example.com", "a", ".someClass");
        assertThat(result.isPresent()).isFalse();
    }

    private void givenHttpClientAndExtractorReturn(Optional<String> finalCode) {
        when(httpClient.getHtml(eq("http://www.example.com"), eq("a"))).thenReturn(of("someHtml"));
        when(selectorExtractor.extract(eq(".someClass"), eq("someHtml"))).thenReturn(finalCode);
    }
}