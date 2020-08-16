package io.kyouin.moemoesongs.core;

import io.kyouin.moemoesongs.entities.Entry;
import io.kyouin.moemoesongs.enums.EntryType;
import io.kyouin.moemoesongs.utils.HtmlUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class MoeCore {

    private static MoeCore instance = null;

    private List<Entry> animeEntries = null;

    private List<Entry> gameEntries = null;

    private MoeCore() {
        //nothing
    }

    public synchronized static MoeCore getInstance() {
        if (instance == null) {
            instance = new MoeCore();
        }

        return instance;
    }

    public List<Entry> getAnimeEntries() {
        if (animeEntries == null) {
            updateAnimeEntries(MoeConstants.DEFAULT_CONCURRENT_THREADS);
        }

        return animeEntries;
    }

    public List<Entry> getGameEntries() {
        if (gameEntries == null) {
            updateGameEntries();
        }

        return gameEntries;
    }

    public void updateAnimeEntries(int concurrentThreads) {
        try {
            Element index = HtmlUtils.getBody("https://www.reddit.com/r/AnimeThemes/wiki/anime_index");
            Elements entries = index.select("div.md.wiki > p");

            List<Entry> tempEntries = new ArrayList<>();
            List<String> urls = entries.stream().skip(1)
                    .map(entry -> entry.selectFirst("a").absUrl("abs:href").split("#")[0])
                    .distinct().collect(Collectors.toList());

            ExecutorService executorService = Executors.newFixedThreadPool(concurrentThreads);
            urls.forEach(url -> executorService.execute(() -> {
                Element body = HtmlUtils.getBody(url);

                tempEntries.addAll(entries.stream().skip(1)
                        .filter(entry -> entry.selectFirst("a").absUrl("abs:href").split("#")[0].equals(url))
                        .map(entry -> body.getElementById(entry.selectFirst("a").absUrl("abs:href").split("#")[1]))
                        .filter(Objects::nonNull)
                        .map(h3 -> Entry.fromElement(h3, EntryType.ANIME))
                        .collect(Collectors.toList()));
            }));

            executorService.shutdown();
            executorService.awaitTermination(15, TimeUnit.MINUTES);

            animeEntries = tempEntries;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAnimeEntries() {
        updateAnimeEntries(MoeConstants.DEFAULT_CONCURRENT_THREADS);
    }

    public void updateGameEntries() {
        Element index = HtmlUtils.getBody("https://www.reddit.com/r/AnimeThemes/wiki/misc");
        Elements entries = index.select("div.md.wiki > h3");

        gameEntries = entries.stream().skip(1)
                .map(entry -> Entry.fromElement(entry, EntryType.GAME))
                .collect(Collectors.toList());
    }
}
