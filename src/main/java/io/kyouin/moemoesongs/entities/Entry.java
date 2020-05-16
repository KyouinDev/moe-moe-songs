package io.kyouin.moemoesongs.entities;

import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Collectors;

public class Entry {

    public enum EntryType {
        ANIME, GAME
    }

    private final EntryType type;
    private final String title;
    private final String alternateTitle;
    private final List<EntrySong> songs;
    private final String link;

    public Entry(Element element, EntryType entryType) {
        type = entryType;
        title = element.text().trim();
        link = element.selectFirst("a") == null ? null : element.selectFirst("a").attr("href");

        element = element.nextElementSibling();

        if (element.tagName().equals("p")) {
            alternateTitle = element.text().trim();

            element = element.nextElementSibling();
        } else alternateTitle = null;

        songs = element.select("tbody > tr").stream()
                .filter(e -> !e.selectFirst("td").text().isEmpty() && !e.select("td").get(1).select("a").isEmpty())
                .map(song -> new EntrySong(this, song))
                .collect(Collectors.toList());
    }

    public EntryType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getAlternateTitle() {
        return alternateTitle;
    }

    public List<EntrySong> getSongs() {
        return songs;
    }

    public String getLink() {
        return link;
    }
}
