package com.ebicep.warlords.util.java;

import java.util.Locale;
import java.util.regex.Pattern;

public class StringUtils {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("[\\s_-]+");
    private static final Pattern NON_ALPHANUMERIC_PATTERN = Pattern.compile("[^a-zA-Z0-9]");

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

    public static String formatTimeLeft(long seconds) {
        StringBuilder message = new StringBuilder();
        formatTimeLeft(message, seconds);
        return message.toString();
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

    public static String toCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        String processed = WHITESPACE_PATTERN.matcher(input.trim()).replaceAll(" ");

        String[] words = processed.split(" ");
        StringBuilder result = new StringBuilder();

        if (words.length > 0) {
            // first word lowercase
            String firstWord = NON_ALPHANUMERIC_PATTERN.matcher(words[0]).replaceAll("");
            if (!firstWord.isEmpty()) {
                result.append(Character.toLowerCase(firstWord.charAt(0)));
                if (firstWord.length() > 1) {
                    result.append(firstWord.substring(1).toLowerCase());
                }
            }

            // other words uppercase
            for (int i = 1; i < words.length; i++) {
                String word = NON_ALPHANUMERIC_PATTERN.matcher(words[i]).replaceAll("");
                if (!word.isEmpty()) {
                    result.append(Character.toUpperCase(word.charAt(0)));
                    if (word.length() > 1) {
                        result.append(word.substring(1).toLowerCase());
                    }
                }
            }
        }

        return result.toString();
    }

    public static String toPlural(String str, int count) {
        return count == 1 ? str : str + "s";
    }

}
