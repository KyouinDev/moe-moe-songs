package io.kyouin.moemoesongs.utils;

import java.util.HashMap;
import java.util.Map;

public class FilterOptions {

    public enum FilterType {
        VERSION, SOURCE, SONG_TITLE
    }

    private final Map<FilterType, String> filterOptions = new HashMap<>();

    private void filterOption(FilterType filterType, String partialString) {
        filterOptions.put(filterType, partialString);
    }

    public FilterOptions filterTitle(String partialTitle) {
        filterOption(FilterType.VERSION, partialTitle);

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

    public Map<FilterType, String> getFilterOptions() {
        return filterOptions;
    }
}
