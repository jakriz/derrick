package io.github.jakriz.derrick.downloader;

public final class UrlHelper {

    public static String makeFromUrlAndPath(String url, String path) {
        if (url == null) {
            throw new IllegalArgumentException("Url must not be null");
        }

        if (path == null) {
            path = "";
        }

        if (!url.endsWith("/") && !path.startsWith("/")) {
            return url + "/" + path;
        } else if (url.endsWith("/") && path.startsWith("/")) {
            return url.substring(0, url.length()-1) + path;
        } else {
            return url + path;
        }
    }

}
