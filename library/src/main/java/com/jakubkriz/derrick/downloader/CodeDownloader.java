package com.jakubkriz.derrick.downloader;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Optional;

public class CodeDownloader {

    private HttpClient httpClient;
    private JsoupSelectorExtractor selectorExtractor;

    public CodeDownloader(HttpClient httpClient, JsoupSelectorExtractor selectorExtractor) {
        this.httpClient = httpClient;
        this.selectorExtractor = selectorExtractor;
    }

    public Optional<String> getMethodCode(String baseUrl, String path, String selector) {
        Optional<String> html = httpClient.getHtml(baseUrl, path);
        if (html.isPresent()) {
            Optional<String> extracted = selectorExtractor.extract(selector, html.get());
            return extracted.map(StringEscapeUtils::unescapeHtml4);
        }
        return Optional.empty();
    }

}
