package io.kyouin.opmoemoe.core;

import io.kyouin.opmoemoe.entities.MoeEntry;
import io.kyouin.opmoemoe.entities.MoeEntryDetails;
import io.kyouin.opmoemoe.requests.Request;

import java.util.ArrayList;
import java.util.List;

public class MoeCore {

    public static MoeEntryDetails getDetails(String filename) {
        return Request.get(Request.METHODS.getDetails(removeExtension(filename)));
    }

    public static List<String> getFilenames(Boolean shuffle, String firstFilename) {
        if (firstFilename != null) firstFilename = removeExtension(firstFilename);

        List<String> filenames = Request.get(Request.METHODS.getFilenames(shuffle, true, firstFilename));

        if (filenames == null) return new ArrayList<>();

        return filenames;
    }

    public static List<String> getFilenames() {
        return getFilenames(null, null);
    }

    public static List<MoeEntry> getResults(Boolean shuffle, String firstFilename) {
        if (firstFilename != null) firstFilename = removeExtension(firstFilename);

        List<MoeEntry> entries = Request.get(Request.METHODS.getResults(shuffle, firstFilename));

        if (entries == null) return new ArrayList<>();

        return entries;
    }

    public static List<MoeEntry> getResults() {
        return getResults(null, null);
    }

    private static String removeExtension(String filename) {
        return filename.replaceAll("\\.(mp4|webm)", "");
    }
}
