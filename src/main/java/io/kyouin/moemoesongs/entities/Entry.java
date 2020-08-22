package io.kyouin.moemoesongs.entities;

import io.kyouin.moemoesongs.enums.EntryType;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

public class Entry {

    private static final String HEADER_LINKS = "h3 > a";
    private static final String TABLE_HEADERS = "table > * th";
    private static final String TABLE_ROWS = "table > tbody > tr";

    private static final String SONG_VERSION = "td:nth-child(1)";
    private static final String SONG_TITLE = "td:nth-child(2) > a";

    private final EntryType type;
    private final String link;
    private final String title;
    private final String alternateTitle;
    private List<EntrySong> songs;

    public static Entry fromElement(Element h3, EntryType type) {
        Element linkElement = h3.selectFirst(HEADER_LINKS);
        String link = null;

        if (linkElement != null) {
            link = linkElement.attr("href");
        }

        String title = h3.text().trim();
        String alternateTitle = null;

        Element nextElement = h3.nextElementSibling();

        if (nextElement.tagName().equals("p")) {
            alternateTitle = nextElement.text().trim();
            nextElement = nextElement.nextElementSibling();
        }

        Entry entry = new Entry(type, link, title, alternateTitle);
        Elements tableHeaders = nextElement.select(TABLE_HEADERS);

        entry.setSongs(nextElement.select(TABLE_ROWS).stream()
                .filter(row -> !row.select(SONG_VERSION).text().isEmpty())
                .filter(row -> !row.select(SONG_TITLE).isEmpty())
                .map(row -> EntrySong.fromElement(row, tableHeaders, entry))
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

    private void setSongs(List<EntrySong> songs) {
        this.songs = songs;
    }
}
