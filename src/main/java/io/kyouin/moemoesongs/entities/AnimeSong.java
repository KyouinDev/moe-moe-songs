package io.kyouin.moemoesongs.entities;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AnimeSong {

    private Anime source;
    private String title;
    private String version;
    private String link;

    public AnimeSong(Anime source, Element tableRow) {
        this.source = source;

        Elements cells = tableRow.select("td");
        String rawTitle = cells.get(0).text();

        title = rawTitle.substring(rawTitle.indexOf('"') + 1, rawTitle.length() - 1);
        version = rawTitle.replace("\"" + title + "\"", "").trim();
        link = cells.get(1).selectFirst("a").attr("href");
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
}
