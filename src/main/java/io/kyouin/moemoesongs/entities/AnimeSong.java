package io.kyouin.moemoesongs.entities;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnimeSong {

    private final Anime source;
    private final String title;
    private final String version;
    private final String link;
    private final String episodes;
    private final String notes;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public AnimeSong(Anime anime, Element tableRow) {
        Elements cells = tableRow.select("td");
        Matcher matcher = Pattern.compile("(.+) ?\"(.+)?\".*").matcher(cells.get(0).text());
        matcher.matches();

        source = anime;
        title = matcher.group(2);
        version = matcher.group(1);
        link = cells.get(1).selectFirst("a").attr("href");

        if (cells.size() > 2) episodes = cells.get(2).text().isEmpty() ? null : cells.get(2).text();
        else episodes = null;

        if (cells.size() > 3) notes = cells.get(3).text().isEmpty() ? null : cells.get(3).text();
        else notes = null;
    }

    public Anime getSource() {
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

    public String getEpisodes() {
        return episodes;
    }

    public String getNotes() {
        return notes;
    }
}
