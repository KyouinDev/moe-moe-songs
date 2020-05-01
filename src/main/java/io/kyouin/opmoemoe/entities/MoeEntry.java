package io.kyouin.opmoemoe.entities;

import java.util.List;

public class MoeEntry {

    private String title;
    private String source;
    private String file;
    private List<String> mime;
    private MoeSong song;

    public String getTitle() {
        return title;
    }

    public String getSource() {
        return source;
    }

    public String getFile() {
        return file;
    }

    public List<String> getMimes() {
        return mime;
    }

    public MoeSong getSong() {
        return song;
    }
}
