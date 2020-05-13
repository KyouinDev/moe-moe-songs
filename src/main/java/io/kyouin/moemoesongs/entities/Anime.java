package io.kyouin.moemoesongs.entities;

import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Collectors;

public class Anime {

    private String title;
    private String alternateTitle;
    private List<AnimeSong> songs;

    public Anime(Element element) {
        title = element.text();

        element = element.nextElementSibling();

        if (element.tagName().equals("p")) {
            alternateTitle = element.text();
            element = element.nextElementSibling();
        }

        songs = element.select("tbody > tr").stream()
                .filter(e -> !e.selectFirst("td").text().isEmpty() && !e.select("td").get(1).select("a").isEmpty())
                .map(song -> new AnimeSong(this, song))
                .collect(Collectors.toList());

    }

    public String getTitle() {
        return title;
    }

    public String getAlternateTitle() {
        return alternateTitle;
    }

    public List<AnimeSong> getSongs() {
        return songs;
    }
}
