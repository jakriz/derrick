package com.jakubkriz.derrick.downloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CodeDownloader {

    private HttpClient httpClient;

    public CodeDownloader(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String getMethodCode(String url, String selector) throws IOException {
        String html = httpClient.getHtml(url);
        if (html != null) {
            Document document = Jsoup.parse(html);
            Elements elements = document.select(selector);
            if (elements != null) {
                return elements.html();
            }
        }
        return null;
    }

}
