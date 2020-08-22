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

    private static final int DEFAULT_CONCURRENT_THREADS = 4;

    private static final String ANIME_INDEX = "https://www.reddit.com/r/AnimeThemes/wiki/anime_index";
    private static final String MISC_INDEX = "https://www.reddit.com/r/AnimeThemes/wiki/misc";

    private static final String WIKI_PARAGRAPHS = "div.md.wiki > p";
    private static final String WIKI_HEADERS = "div.md.wiki > h3";

    private static final String PARAGRAPH_LINKS = "p > a";

    private static final MoeCore instance = new MoeCore();

    private List<Entry> animeEntries = null;
    private List<Entry> gameEntries = null;

    private MoeCore() {
        //nothing
    }

    public static MoeCore getInstance() {
        return instance;
    }

    public List<Entry> getAnimeEntries() {
        if (animeEntries == null) {
            updateAnimeEntries(DEFAULT_CONCURRENT_THREADS);
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
            Element index = HtmlUtils.getBody(ANIME_INDEX);
            Elements entries = index.select(WIKI_PARAGRAPHS);
            ExecutorService executorService = Executors.newFixedThreadPool(concurrentThreads);

            List<String> urls = entries.stream()
                    .skip(1)
                    .map(entry -> entry.selectFirst(PARAGRAPH_LINKS).absUrl("abs:href").split("#")[0])
                    .distinct()
                    .collect(Collectors.toList());

            List<Entry> tempEntries = new ArrayList<>();

            urls.forEach(url -> executorService.execute(() -> {
                Element body = HtmlUtils.getBody(url);

                tempEntries.addAll(entries.stream()
                        .skip(1)
                        .filter(entry -> entry.selectFirst(PARAGRAPH_LINKS).absUrl("abs:href").split("#")[0].equals(url))
                        .map(entry -> body.getElementById(entry.selectFirst(PARAGRAPH_LINKS).absUrl("abs:href").split("#")[1]))
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
        updateAnimeEntries(DEFAULT_CONCURRENT_THREADS);
    }

    public void updateGameEntries() {
        Element index = HtmlUtils.getBody(MISC_INDEX);
        Elements entries = index.select(WIKI_HEADERS);

        gameEntries = entries.stream().skip(1)
                .map(entry -> Entry.fromElement(entry, EntryType.GAME))
                .collect(Collectors.toList());
    }
}
