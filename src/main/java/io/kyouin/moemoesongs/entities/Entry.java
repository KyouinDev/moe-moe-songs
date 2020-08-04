package io.kyouin.moemoesongs.entities;

import io.kyouin.moemoesongs.enums.EntryType;

import java.util.List;

@SuppressWarnings("unused")
public class Entry {

    private final EntryType type;
    private final String link;
    private final String title;
    private final String alternateTitle;
    private List<EntrySong> songs;

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
