package io.kyouin.moemoesongs.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class HtmlUtils {

    public static Element getBody(String url) {
        Document doc = null;

        try {
            doc = Jsoup.connect(url).timeout(30 * 1000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (doc == null) throw new IllegalArgumentException("Document could not be get: " + url);

        return doc.body();
    }
}
