package io.kyouin.moemoesongs.utils;

import java.util.Locale;
import java.util.regex.Pattern;

public final class StringUtils {

    private StringUtils() {
        //nothing
    }

    public static boolean partialMatch(String partial, String complete) {
        if (partial == null || complete == null || partial.length() > complete.length()) {
            return false;
        }

        Pattern pattern = Pattern.compile(partial.toLowerCase(Locale.ENGLISH), Pattern.CASE_INSENSITIVE);

        return pattern.matcher(complete).find();
    }
}
