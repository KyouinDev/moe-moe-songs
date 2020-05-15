package io.kyouin.moemoesongs.core;

import io.kyouin.moemoesongs.entities.Anime;
import io.kyouin.moemoesongs.entities.AnimeSong;
import io.kyouin.moemoesongs.utils.FilterOptions;
import io.kyouin.moemoesongs.utils.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

    private static List<Anime> animeList = new ArrayList<>();

    // Get methods

    public static List<Anime> getAnimeList() {
        return animeList;
    }

    public static List<AnimeSong> getOpenings() {
        List<AnimeSong> filtered = new ArrayList<>();

        animeList.forEach(anime -> anime.getSongs().forEach(animeSong -> {
            if (StringUtils.partialMatch("op", animeSong.getVersion())) filtered.add(animeSong);
        }));

        return filtered;
    }

    public static List<AnimeSong> getEndings() {
        List<AnimeSong> filtered = new ArrayList<>();

        animeList.forEach(anime -> anime.getSongs().forEach(animeSong -> {
            if (StringUtils.partialMatch("ed", animeSong.getVersion())) filtered.add(animeSong);
        }));

        return filtered;
    }

    public static List<AnimeSong> getFromTitle(String animeTitle) {
        List<AnimeSong> filtered = new ArrayList<>();

        animeList.stream()
                .filter(anime -> StringUtils.partialMatch(animeTitle, anime.getTitle()) || (anime.getAlternateTitle() != null && StringUtils.partialMatch(animeTitle, anime.getAlternateTitle())))
                .forEach(anime -> filtered.addAll(anime.getSongs()));

        return filtered;
    }

    public static List<AnimeSong> getFromSongTitle(String songTitle) {
        List<AnimeSong> filtered = new ArrayList<>();

        animeList.forEach(anime -> anime.getSongs().forEach(animeSong -> {
            if (StringUtils.partialMatch(songTitle, animeSong.getTitle())) filtered.add(animeSong);
        }));

        return filtered;
    }

    public static List<AnimeSong> getFromFilter(FilterOptions filterOptions) {
        Map<FilterOptions.FilterType, String> options = filterOptions.getFilterOptions();
        List<AnimeSong> filtered = new ArrayList<>();

        animeList.forEach(anime -> {
            if (options.containsKey(FilterOptions.FilterType.SOURCE) && StringUtils.partialMatch(options.get(FilterOptions.FilterType.SOURCE), anime.getTitle()) || (anime.getAlternateTitle() != null && StringUtils.partialMatch(options.get(FilterOptions.FilterType.SOURCE), anime.getAlternateTitle()))) {
                filtered.addAll(anime.getSongs());

                return;
            }

            anime.getSongs().forEach(animeSong -> {
                if (options.containsKey(FilterOptions.FilterType.VERSION) && StringUtils.partialMatch(options.get(FilterOptions.FilterType.VERSION), animeSong.getVersion())) {
                    filtered.add(animeSong);
                } else if (options.containsKey(FilterOptions.FilterType.SONG_TITLE) && StringUtils.partialMatch(options.get(FilterOptions.FilterType.SONG_TITLE), animeSong.getTitle())) {
                    filtered.add(animeSong);
                }
            });
        });

        return filtered;
    }

    // Update methods

    public static void updateAnimeList(int concurrentThreads) {
        List<Anime> animeTempList = new ArrayList<>();
        Document animeIndex = null;

        try {
            animeIndex = Jsoup.connect("https://www.reddit.com/r/AnimeThemes/wiki/anime_index").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (animeIndex == null) throw new IllegalArgumentException("Anime index could not be get.");

        Elements animeEntries = animeIndex.selectFirst("div.md.wiki").select("p");
        List<String> animeUrls = animeEntries.stream().skip(1)
                .map(animeEntry -> animeEntry.selectFirst("a").absUrl("abs:href").split("#")[0])
                .distinct()
                .collect(Collectors.toList());
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentThreads);

        animeUrls.forEach(url -> executorService.execute(() -> {
            try {
                Document tempDoc = null;

                while (tempDoc == null) {
                    tempDoc = Jsoup.connect(url).get();
                }

                Document doc = tempDoc;

                animeEntries.stream().skip(1).forEachOrdered(animeEntry -> {
                    String rawUrl = animeEntry.selectFirst("a").absUrl("abs:href");
                    String docUrl = rawUrl.split("#")[0];

                    if (!docUrl.equals(url)) return;

                    String id = rawUrl.split("#")[1];
                    Element h3 = doc.getElementById(id);

                    if (h3 != null) animeTempList.add(new Anime(h3));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        try {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (animeTempList.size() > animeList.size()) animeList = animeTempList;
    }
}
