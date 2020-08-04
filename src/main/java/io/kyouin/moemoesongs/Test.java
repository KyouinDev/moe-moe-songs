package io.kyouin.moemoesongs;

import io.kyouin.moemoesongs.core.MoeCore;
import io.kyouin.moemoesongs.entities.EntrySong;
import io.kyouin.moemoesongs.filter.EntryFilter;

import java.util.List;

public class Test {

    public static void main(String[] args) {
        long ms = System.currentTimeMillis();

        System.out.println(MoeCore.getInstance().getAnimeEntries().size() + " anime loaded");
        System.out.println(MoeCore.getInstance().getGameEntries().size() + " games loaded");

        System.out.println((System.currentTimeMillis() - ms) / 1000 + " seconds elapsed");

        List<EntrySong> filtered = EntryFilter
                .anime()
                .filterEntryTitle("Steins")
                .filterSongVersion("ED")
                .result();

        System.out.println("Found " + filtered.size() + " results:");
        filtered.forEach(song -> System.out.println(song.getTitle() + ", " + song.getVersion()));
    }
}
