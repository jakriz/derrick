package com.jakubkriz.derrick.downloader;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Optional;

public class OkhttpHttpClient implements HttpClient {

    private OkHttpClient client = new OkHttpClient();

    @Override
    public Optional<String> getHtml(String url, String path) {
        Request request = new Request.Builder()
                .url(UrlHelper.makeFromUrlAndPath(url, path))
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            return (response.isSuccessful() ? Optional.of(response.body().string()) : Optional.empty());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

}
