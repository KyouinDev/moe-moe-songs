package io.kyouin.moemoesongs.entities;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntrySong {

    private final Entry source;
    private final String title;
    private final String version;
    private final String link;
    private String where;
    private String notes;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public EntrySong(Entry entry, Element tableRow) {
        Elements cells = tableRow.select("td");
        Matcher matcher = Pattern.compile("(.+) ?\"(.+)?\".*").matcher(cells.get(0).text());
        matcher.matches();

        source = entry;
        title = matcher.group(2);
        version = matcher.group(1).trim();
        link = cells.get(1).selectFirst("a").attr("href");

        if (cells.size() == 4) {
            where = cells.get(2).text().isEmpty() ? null : cells.get(2).text();
            notes = cells.get(3).text().isEmpty() ? null : cells.get(3).text();
        } else if (cells.size() == 3 && !cells.get(2).text().isEmpty()) {
            if (source.getType() == Entry.EntryType.ANIME) where = cells.get(2).text();
            else notes = cells.get(2).text();
        }
    }

    public Entry getSource() {
        return source;
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
