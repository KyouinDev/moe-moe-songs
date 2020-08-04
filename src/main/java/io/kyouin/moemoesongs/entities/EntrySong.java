package io.kyouin.moemoesongs.entities;

import java.util.List;

@SuppressWarnings("unused")
public class EntrySong {

    private final Entry entry;
    private final String title;
    private final String version;
    private final List<String> links;
    private final String where;
    private final String notes;

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
