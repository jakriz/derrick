package com.jakubkriz.derrick.downloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Optional;

public class JsoupSelectorExtractor implements SelectorExtractor {

    public Optional<String> extract(String cssSelector, String html) {
        Document document = Jsoup.parse(html);
        Elements elements = document.select(cssSelector);
        return (elements != null ? Optional.of(elements.html()) : Optional.empty());
    }

}
