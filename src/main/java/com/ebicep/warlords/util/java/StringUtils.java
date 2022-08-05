package com.ebicep.warlords.util.java;

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

}
