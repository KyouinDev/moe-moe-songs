package io.kyouin.opmoemoe.utils;

import java.util.regex.Pattern;

public class StringUtils {

    public static String removeExtension(String filename) {
        return filename.replaceAll("\\.(mp4|webm)", "");
    }

    public static boolean partialMatch(String partial, String complete) {
        Pattern pattern = Pattern.compile(partial.toLowerCase(), Pattern.CASE_INSENSITIVE);

        return pattern.matcher(complete).find();
    }
}
