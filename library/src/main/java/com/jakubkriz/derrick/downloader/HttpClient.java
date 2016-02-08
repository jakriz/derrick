package com.jakubkriz.derrick.downloader;

import java.util.Optional;

public interface HttpClient {

    Optional<String> getHtml(String url, String path);
}
