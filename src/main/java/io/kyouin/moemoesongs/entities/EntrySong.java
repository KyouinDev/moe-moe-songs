package io.kyouin.moemoesongs.entities;

import io.kyouin.moemoesongs.enums.EntryType;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntrySong {

    private final Entry entry;
    private final String title;
    private final String version;
    private final List<String> links;
    private final String where;
    private final String notes;

    public static EntrySong fromElement(Element element, Entry entry) {
        Elements cells = element.select("td");
        Matcher matcher = Pattern.compile("(.+) ?\"(.+)?\".*").matcher(cells.get(0).text().trim());

        if (!matcher.matches()) throw new IllegalStateException("Could not match song title '" + cells.get(0).text().trim() + "'");

        String title = matcher.group(2);
        String version = matcher.group(1);
        List<String> links = Collections.singletonList(cells.get(1).selectFirst("a").attr("href"));
        String where = null;
        String notes = null;

        String cell2Text = cells.size() < 3 || cells.get(2).text().isEmpty() ? null : cells.get(2).text().trim();
        String cell3Text = cells.size() < 4 || cells.get(3).text().isEmpty() ? null : cells.get(3).text().trim();

        if (cells.size() == 4) {
            where = cell2Text;
            notes = cell3Text;
        } else if (cells.size() == 3) {
            if (entry.getType() == EntryType.ANIME) where = cell2Text;
            else notes = cell2Text;
        }

        return new EntrySong(entry, title, version, links, where, notes);
    }

    public EntrySong(Entry entry, String title, String version, List<String> links, String where, String notes) {
        this.entry = entry;
        this.title = title;
        this.version = version;
        this.links = links;
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

    public List<String> getLinks() {
        return links;
    }

    public String getWhere() {
        return where;
    }

    public String getNotes() {
        return notes;
    }
}
