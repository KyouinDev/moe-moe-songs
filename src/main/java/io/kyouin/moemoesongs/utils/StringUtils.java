package io.kyouin.moemoesongs.utils;

import java.util.regex.Pattern;

public class StringUtils {

    public static boolean partialMatch(String partial, String complete) {
        Pattern pattern = Pattern.compile(partial.toLowerCase(), Pattern.CASE_INSENSITIVE);

        return pattern.matcher(complete).find();
    }
}
