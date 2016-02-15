package io.github.jakriz.derrick.downloader;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Optional;

public class OkhttpHttpClient implements HttpClient {

    private OkHttpClient client;

    public OkhttpHttpClient() {
        this.client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .build();
    }

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
