package io.kyouin.moemoesongs.core;

import io.kyouin.moemoesongs.entities.Entry;
import io.kyouin.moemoesongs.entities.EntrySong;
import io.kyouin.moemoesongs.utils.FilterOptions;
import io.kyouin.moemoesongs.utils.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MoeCore {

    private static List<Entry> entryList = new ArrayList<>();

    public static List<Entry> getEntryList() {
        return entryList;
    }

    // Get methods

    public static List<Entry> getAnimeList() {
        return entryList.stream().filter(entry -> entry.getType() == Entry.EntryType.ANIME).collect(Collectors.toList());
    }

    public static List<Entry> getGameList() {
        return entryList.stream().filter(entry -> entry.getType() == Entry.EntryType.GAME).collect(Collectors.toList());
    }

    public static List<EntrySong> getOpenings() {
        List<EntrySong> filtered = new ArrayList<>();

        entryList.forEach(entry -> filtered.addAll(entry.getSongs().stream()
                .filter(song -> StringUtils.partialMatch("op", song.getVersion()))
                .collect(Collectors.toList())));

        return filtered;
    }

    public static List<EntrySong> getEndings() {
        List<EntrySong> filtered = new ArrayList<>();

        entryList.forEach(entry -> filtered.addAll(entry.getSongs().stream()
                .filter(song -> StringUtils.partialMatch("ed", song.getVersion()))
                .collect(Collectors.toList())));

        return filtered;
    }

    public static List<EntrySong> getFromSource(String partialSource) {
        List<EntrySong> filtered = new ArrayList<>();

        entryList.stream()
                .filter(entry -> StringUtils.partialMatch(partialSource, entry.getTitle()) || (entry.getAlternateTitle() != null && StringUtils.partialMatch(partialSource, entry.getAlternateTitle())))
                .forEach(entry -> filtered.addAll(entry.getSongs()));

        return filtered;
    }

    public static List<EntrySong> getFromSongTitle(String partialSongTitle) {
        List<EntrySong> filtered = new ArrayList<>();

        entryList.forEach(entry -> filtered.addAll(entry.getSongs().stream()
                .filter(song -> StringUtils.partialMatch(partialSongTitle, song.getTitle()))
                .collect(Collectors.toList())));

        return filtered;
    }

    public static List<EntrySong> getFromFilter(FilterOptions filterOptions) {
        Map<FilterOptions.FilterType, String> options = filterOptions.getFilterOptions();
        List<EntrySong> filtered = new ArrayList<>();

        entryList.forEach(entry -> {
            if (options.containsKey(FilterOptions.FilterType.SOURCE) && (StringUtils.partialMatch(options.get(FilterOptions.FilterType.SOURCE), entry.getTitle()) || StringUtils.partialMatch(options.get(FilterOptions.FilterType.SOURCE), entry.getAlternateTitle()))) {
                filtered.addAll(entry.getSongs());

                return;
            }

            entry.getSongs().forEach(song -> {
                if (options.containsKey(FilterOptions.FilterType.VERSION) && StringUtils.partialMatch(options.get(FilterOptions.FilterType.VERSION), song.getVersion())) {
                    filtered.add(song);
                } else if (options.containsKey(FilterOptions.FilterType.SONG_TITLE) && StringUtils.partialMatch(options.get(FilterOptions.FilterType.SONG_TITLE), song.getTitle())) {
                    filtered.add(song);
                }
            });
        });

        return filtered;
    }

    // Update methods

    private static Element getBody(String url) {
        Element body = null;

        try {
            body = Jsoup.connect(url).timeout(30 * 1000).get().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (body == null) throw new IllegalArgumentException("Body could not be get.");

        return body;
    }

    public static void updateSongList(int concurrentThreads) {
        List<Entry> tempEntryList = new ArrayList<>();

        Element animeIndex = getBody("https://www.reddit.com/r/AnimeThemes/wiki/anime_index");
        Elements animeEntries = animeIndex.selectFirst("div.md.wiki").select("p");
        List<String> animeUrls = animeEntries.stream().skip(1)
                .map(animeEntry -> animeEntry.selectFirst("a").absUrl("abs:href").split("#")[0])
                .distinct().collect(Collectors.toList());

        ExecutorService executorService = Executors.newFixedThreadPool(concurrentThreads);
        animeUrls.forEach(url -> executorService.execute(() -> {
            Element body = getBody(url);

            animeEntries.stream().skip(1).forEachOrdered(animeEntry -> {
                String rawUrl = animeEntry.selectFirst("a").absUrl("abs:href");
                String docUrl = rawUrl.split("#")[0];

                if (!docUrl.equals(url)) return;

                String id = rawUrl.split("#")[1];
                Element h3 = body.getElementById(id);

                if (h3 != null) tempEntryList.add(new Entry(h3, Entry.EntryType.ANIME));
            });
        }));

        try {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Element gameIndex = getBody("https://www.reddit.com/r/AnimeThemes/wiki/misc");
        Elements gameEntries = gameIndex.selectFirst("div.md.wiki").select("h3");
        gameEntries.stream().skip(1).forEachOrdered(gameEntry -> tempEntryList.add(new Entry(gameEntry, Entry.EntryType.GAME)));

        if (tempEntryList.size() > entryList.size()) entryList = tempEntryList;
    }
}
