package io.kyouin.moemoesongs.filters;

import io.kyouin.moemoesongs.core.MoeCore;
import io.kyouin.moemoesongs.entities.Entry;
import io.kyouin.moemoesongs.entities.EntrySong;
import io.kyouin.moemoesongs.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class EntryFilter {

    private Stream<EntrySong> songs;

    public static EntryFilter full() {
        return new EntryFilter(true, true);
    }

    public static EntryFilter anime() {
        return new EntryFilter(true, false);
    }

    public static EntryFilter game() {
        return new EntryFilter(false, true);
    }

    private EntryFilter(boolean isAnime, boolean isGame) {
        List<Entry> temp = new ArrayList<>();

        if (isAnime) temp.addAll(MoeCore.getInstance().getAnimeEntries());
        if (isGame) temp.addAll(MoeCore.getInstance().getGameEntries());

        songs = temp.stream().flatMap(entry -> entry.getSongs().stream());
    }

    public EntryFilter filterEntryTitle(String entryTitle) {
        songs = songs.filter(entry -> StringUtils.partialMatch(entryTitle, entry.getEntry().getTitle()));

        return this;
    }

    public EntryFilter filterEntryAlternateTitle(String entryAlternateTitle) {
        songs = songs.filter(entry -> StringUtils.partialMatch(entryAlternateTitle, entry.getEntry().getAlternateTitle()));

        return this;
    }

    public EntryFilter filterSongTitle(String songTitle) {
        songs = songs.filter(song -> StringUtils.partialMatch(songTitle, song.getTitle()));

        return this;
    }

    public EntryFilter filterSongVersion(String songVersion) {
        songs = songs.filter(song -> StringUtils.partialMatch(songVersion, song.getVersion()));

        return this;
    }

    public List<EntrySong> result() {
        return songs.collect(Collectors.toList());
    }
}
