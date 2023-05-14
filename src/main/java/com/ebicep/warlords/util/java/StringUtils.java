package com.ebicep.warlords.util.java;

import java.util.Locale;

public class StringUtils {

    /**
     * Splits string n times, if n is greater than string length then it will return string split its length times
     *
     * @param str String to split
     * @param n   Number of splits
     * @return Splitted string
     */
    public static String[] splitStringNTimes(String str, int n) {
        String[] result;
        if (n >= str.length()) {
            result = new String[str.length()];
            for (int i = 0; i < str.length(); i++) {
                result[i] = str.charAt(i) + "";
            }
        } else {
            result = new String[n];
            int length = str.length();
            for (int i = 0; i < n; i++) {
                result[i] = str.substring(i * length / n, (i + 1) * length / n);
            }
        }
        return result;
    }

    public static void formatTimeLeft(StringBuilder message, long seconds) {
        long minute = seconds / 60;
        long second = seconds % 60;
        if (minute < 10) {
            message.append('0');
        }
        message.append(minute);
        message.append(':');
        if (second < 10) {
            message.append('0');
        }
        message.append(second == -1 ? 0 : second);
    }

    public static String formatTimeLeft(long seconds) {
        StringBuilder message = new StringBuilder();
        formatTimeLeft(message, seconds);
        return message.toString();
    }

    public static String toTitleCase(Object input) {
        return toTitleCase(String.valueOf(input));
    }

    public static String toTitleCase(String input) {
        return input.substring(0, 1).toUpperCase(Locale.ROOT) + input.substring(1).toLowerCase(Locale.ROOT);
    }

    public static String toTitleHumanCase(Object input) {
        return toTitleHumanCase(String.valueOf(input));
    }

    public static String toTitleHumanCase(String input) {
        return input.substring(0, 1).toUpperCase(Locale.ROOT) + input.replace('_', ' ').substring(1).toLowerCase(Locale.ROOT);
    }

    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return str.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}
