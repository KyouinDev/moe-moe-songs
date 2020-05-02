package io.kyouin.opmoemoe.utils;

import java.util.HashMap;
import java.util.Map;

public class FilterOptions {

    public enum FilterType {
        TITLE, SOURCE, SONG_TITLE, SONG_ARTIST
    }

    private final Map<FilterType, String> filterOptions = new HashMap<>();

    private void filterOption(FilterType filterType, String partialString) {
        filterOptions.put(filterType, partialString);
    }

    public FilterOptions filterTitle(String partialTitle) {
        filterOption(FilterType.TITLE, partialTitle);

        return this;
    }

    public FilterOptions filterSource(String partialSource) {
        filterOption(FilterType.SOURCE, partialSource);

        return this;
    }

    public FilterOptions filterSongTitle(String partialSongTitle) {
        filterOption(FilterType.SONG_TITLE, partialSongTitle);

        return this;
    }

    public FilterOptions filterSongArtist(String partialSongArtist) {
        filterOption(FilterType.SONG_ARTIST, partialSongArtist);

        return this;
    }

    public Map<FilterType, String> getFilterOptions() {
        return filterOptions;
    }
}
