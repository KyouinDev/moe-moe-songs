package io.kyouin.moemoesongs.entities;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntrySong {

    private static final String SONG_TITLE = "td:nth-child(1)";
    private static final String SONG_LINK = "td:nth-child(2) > a";
    private static final String SONG_WHERE = "td:nth-child(3)";
    private static final String SONG_NOTES = "td:nth-child(4)";

    private static final String THIRD_COL_HEADER = "th:nth-child(3)";

    private final Entry entry;
    private final String title;
    private final String version;
    private final String link;
    private final String where;
    private final String notes;

    public static EntrySong fromElement(Element tr, Elements tableHeaders, Entry entry) {
        Matcher matcher = Pattern.compile("(.+) ?\"(.+)?\".*").matcher(tr.select(SONG_TITLE).text().trim());

        if (!matcher.matches()) throw new IllegalStateException("Could not match song title '" + tr.select(SONG_TITLE).text().trim() + "'");

        String title = matcher.group(2);
        String version = matcher.group(1);

        if (version != null) {
            version = version.trim();
        }

        String link = tr.selectFirst(SONG_LINK).attr("href");

        if ("Notes".equals(tableHeaders.select(THIRD_COL_HEADER).text().trim())) {
            String notes = tr.select(SONG_NOTES).text().isEmpty() ? null : tr.select(SONG_NOTES).text().trim();

            return new EntrySong(entry, title, version, link, null, notes);
        }

        String where = tr.select(SONG_WHERE).text().isEmpty() ? null : tr.select(SONG_WHERE).text().trim();
        String notes = null;

        if (tableHeaders.size() == 4) {
            notes = tr.select(SONG_NOTES).text().isEmpty() ? null : tr.select(SONG_NOTES).text().trim();
        }

        return new EntrySong(entry, title, version, link, where, notes);
    }

    public EntrySong(Entry entry, String title, String version, String link, String where, String notes) {
        this.entry = entry;
        this.title = title;
        this.version = version;
        this.link = link;
        this.where = where;
        this.notes = notes;
    }

    public Entry getEntry() {
        return entry;
    }

    public String getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }

    public String getLink() {
        return link;
    }

    public String getWhere() {
        return where;
    }

    public String getNotes() {
        return notes;
    }
}
