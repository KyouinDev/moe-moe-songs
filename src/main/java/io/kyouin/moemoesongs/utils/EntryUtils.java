package io.kyouin.moemoesongs.utils;

import io.kyouin.moemoesongs.entities.Entry;
import io.kyouin.moemoesongs.entities.EntrySong;
import io.kyouin.moemoesongs.enums.EntryType;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EntryUtils {

    public static Entry buildEntry(Element element, EntryType type) {
        String link = element.selectFirst("a") == null ? null : element.selectFirst("a").attr("href");
        String title = element.text().trim();
        String alternateTitle = null;

        element = element.nextElementSibling();

        if (element.tagName().equals("p")) {
            alternateTitle = element.text().trim();
            element = element.nextElementSibling();
        }

        Entry entry = new Entry(type, link, title, alternateTitle);
        entry.setSongs(element.select("tbody > tr").stream()
                .filter(e -> !e.selectFirst("td").text().isEmpty() && !e.select("td").get(1).select("a").isEmpty())
                .map(row -> buildSong(row, entry))
                .collect(Collectors.toList()));

        return entry;
    }

    public static EntrySong buildSong(Element element, Entry entry) {
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
}
