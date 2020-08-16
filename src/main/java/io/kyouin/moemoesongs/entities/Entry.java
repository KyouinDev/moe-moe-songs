package io.kyouin.moemoesongs.entities;

import io.kyouin.moemoesongs.enums.EntryType;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Collectors;

public class Entry {

    private final EntryType type;
    private final String link;
    private final String title;
    private final String alternateTitle;
    private List<EntrySong> songs;

    public static Entry fromElement(Element element, EntryType type) {
        String link = element.selectFirst("a") == null ? null : element.selectFirst("a").attr("href");
        String title = element.text().trim();
        String alternateTitle = null;

        Element nextElement = element.nextElementSibling();

        if (nextElement.tagName().equals("p")) {
            alternateTitle = nextElement.text().trim();
            nextElement = nextElement.nextElementSibling();
        }

        Entry entry = new Entry(type, link, title, alternateTitle);
        entry.setSongs(nextElement.select("tbody > tr").stream()
                .filter(e -> !e.selectFirst("td").text().isEmpty() && !e.select("td").get(1).select("a").isEmpty())
                .map(row -> EntrySong.fromElement(row, entry))
                .collect(Collectors.toList()));

        return entry;
    }

    public Entry(EntryType type, String link, String title, String alternateTitle) {
        this.type = type;
        this.link = link;
        this.title = title;
        this.alternateTitle = alternateTitle;
    }

    public EntryType getType() {
        return type;
    }

    public String getLink() {
        return link;
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

    public void setSongs(List<EntrySong> songs) {
        this.songs = songs;
    }
}
