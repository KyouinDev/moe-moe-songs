package io.kyouin.opmoemoe.core;

import io.kyouin.opmoemoe.entities.MoeEntry;
import io.kyouin.opmoemoe.entities.MoeEntryDetails;
import io.kyouin.opmoemoe.requests.Request;
import io.kyouin.opmoemoe.utils.FilterOptions;
import io.kyouin.opmoemoe.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MoeCore {

    // GENERAL METHODS

    public static MoeEntryDetails getDetails(String filename) {
        return Request.get(Request.METHODS.getDetails(StringUtils.removeExtension(filename)));
    }

    public static List<String> getFilenames(Boolean shuffle, String firstFilename) {
        if (firstFilename != null) firstFilename = StringUtils.removeExtension(firstFilename);

        List<String> filenames = Request.get(Request.METHODS.getFilenames(shuffle, true, firstFilename));

        if (filenames == null) return new ArrayList<>();

        return filenames;
    }

    public static List<String> getFilenames() {
        return getFilenames(null, null);
    }

    public static List<MoeEntry> getResults(Boolean shuffle, String firstFilename) {
        if (firstFilename != null) firstFilename = StringUtils.removeExtension(firstFilename);

        List<MoeEntry> entries = Request.get(Request.METHODS.getResults(shuffle, firstFilename));

        if (entries == null) return new ArrayList<>();

        return entries;
    }

    public static List<MoeEntry> getResults() {
        return getResults(null, null);
    }

    // FILTER METHODS

    public static List<MoeEntry> getOpenings() {
        return cachedResults.stream()
                .filter(entry -> StringUtils.partialMatch("opening", entry.getTitle()))
                .collect(Collectors.toList());
    }

    public static List<MoeEntry> getEndings() {
        return cachedResults.stream()
                .filter(entry -> StringUtils.partialMatch("ending", entry.getTitle()))
                .collect(Collectors.toList());
    }

    public static List<MoeEntry> getFromSource(String partialSource) {
        return cachedResults.stream()
                .filter(entry -> StringUtils.partialMatch(partialSource, entry.getSource()))
                .collect(Collectors.toList());
    }

    public static List<MoeEntry> getFromSongTitle(String partialSongTitle) {
        return cachedResults.stream()
                .filter(entry -> entry.getSong() != null && StringUtils.partialMatch(partialSongTitle, entry.getSong().getTitle()))
                .collect(Collectors.toList());
    }

    public static List<MoeEntry> getFromSongArtist(String partialSongArtist) {
        return cachedResults.stream()
                .filter(entry -> entry.getSong() != null && StringUtils.partialMatch(partialSongArtist, entry.getSong().getArtist()))
                .collect(Collectors.toList());
    }

    public static List<MoeEntry> getFromFilter(FilterOptions filterOptions) {
        return cachedResults.stream()
                .filter(entry -> filterOptions.getFilterOptions().entrySet().stream().allMatch(option -> {
                    switch (option.getKey()) {
                        case TITLE: return StringUtils.partialMatch(option.getValue(), entry.getTitle());
                        case SOURCE: return StringUtils.partialMatch(option.getValue(), entry.getSource());
                        case SONG_TITLE: return entry.getSong() != null && StringUtils.partialMatch(option.getValue(), entry.getSong().getTitle());
                        case SONG_ARTIST: return entry.getSong() != null && StringUtils.partialMatch(option.getValue(), entry.getSong().getArtist());
                    }

                    return false;
                })).collect(Collectors.toList());
    }

    // CACHE

    private static List<String> cachedFilenames = getFilenames();

    public static List<String> getCachedFilenames() {
        return cachedFilenames;
    }

    private static List<MoeEntry> cachedResults = getResults();

    public static List<MoeEntry> getCachedResults() {
        return cachedResults;
    }

    public static void updateCache() {
        cachedFilenames = getFilenames();
        cachedResults = getResults();
    }
}
