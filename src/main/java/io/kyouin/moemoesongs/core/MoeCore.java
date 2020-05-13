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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MoeCore {

    private static List<Anime> animeList = new ArrayList<>();

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

    public static List<AnimeSong> getFromTitle(String title) {
        List<AnimeSong> filtered = new ArrayList<>();

        animeList.stream()
                .filter(anime -> StringUtils.partialMatch(title, anime.getTitle()) || (anime.getAlternateTitle() != null && StringUtils.partialMatch(title, anime.getAlternateTitle())))
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

    public static void updateAnimeList() {
        List<Anime> temp = new ArrayList<>();
        Map<String, Document> docs = new HashMap<>();

        Document doc = null;

        try {
            doc = Jsoup.connect("https://www.reddit.com/r/AnimeThemes/wiki/anime_index").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (doc == null) throw new IllegalArgumentException("Anime index could not be get.");

        Elements paragraphs = doc.selectFirst("div.md.wiki").select("p");
        ExecutorService executorService = Executors.newCachedThreadPool();

        paragraphs.stream().skip(1)
                .map(paragraph -> paragraph.selectFirst("a").absUrl("abs:href").split("#")[0])
                .distinct()
                .forEach(url -> executorService.execute(() -> {
                    try {
                        docs.put(url, Jsoup.connect(url).get());
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

        paragraphs.stream().skip(1).forEach(paragraph -> {
            String rawUrl = paragraph.selectFirst("a").absUrl("abs:href");
            Document animeDoc = docs.get(rawUrl.split("#")[0]);

            if (animeDoc == null) return;

            Element h3 = animeDoc.getElementById(rawUrl.split("#")[1]);

            if (h3 == null) return;

            temp.add(new Anime(h3));
        });

        if (temp.size() > animeList.size()) animeList = temp;
    }
}
