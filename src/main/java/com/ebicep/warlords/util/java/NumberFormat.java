package com.ebicep.warlords.util.java;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class NumberFormat {

    private static final DecimalFormat FORMAT_COMMAS = new DecimalFormat("#,###");
    private static final DecimalFormat DECIMAL_FORMAT_TENTHS = new DecimalFormat("0.0");
    private static final DecimalFormat DECIMAL_FORMAT_OPTIONAL_TENTHS = new DecimalFormat("#.#");
    private static final DecimalFormat DECIMAL_FORMAT_OPTIONAL_HUNDREDTHS = new DecimalFormat("#.##");
    private static final DecimalFormat DECIMAL_FORMAT_COMMAS_OPTIONAL_HUNDREDTHS = new DecimalFormat("#,###.##");
    private static final NavigableMap<Long, String> SUFFIXES = new TreeMap<>();

    static {
        DECIMAL_FORMAT_TENTHS.setDecimalSeparatorAlwaysShown(false);
        DECIMAL_FORMAT_OPTIONAL_TENTHS.setDecimalSeparatorAlwaysShown(false);
        DECIMAL_FORMAT_OPTIONAL_HUNDREDTHS.setDecimalSeparatorAlwaysShown(false);
        DECIMAL_FORMAT_COMMAS_OPTIONAL_HUNDREDTHS.setDecimalSeparatorAlwaysShown(false);

        DECIMAL_FORMAT_TENTHS.setRoundingMode(RoundingMode.HALF_UP);
        DECIMAL_FORMAT_OPTIONAL_TENTHS.setRoundingMode(RoundingMode.HALF_UP);
        DECIMAL_FORMAT_OPTIONAL_HUNDREDTHS.setRoundingMode(RoundingMode.HALF_UP);
        DECIMAL_FORMAT_COMMAS_OPTIONAL_HUNDREDTHS.setRoundingMode(RoundingMode.HALF_UP);

        SUFFIXES.put(1_000L, "k");
        SUFFIXES.put(1_000_000L, "m");
        SUFFIXES.put(1_000_000_000L, "b");
        SUFFIXES.put(1_000_000_000_000L, "t");
        SUFFIXES.put(1_000_000_000_000_000L, "p");
        SUFFIXES.put(1_000_000_000_000_000_000L, "e");
    }

    public static String formatOptionalHundredths(double value) {
        return DECIMAL_FORMAT_OPTIONAL_HUNDREDTHS.format(value);
    }

    public static String formatOptionalTenths(double value) {
        return DECIMAL_FORMAT_OPTIONAL_TENTHS.format(value);
    }

    public static String formatTenths(double value) {
        return NumberFormat.DECIMAL_FORMAT_TENTHS.format(value);
    }

    public static String getSimplifiedNumber(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) {
            return getSimplifiedNumber(Long.MIN_VALUE + 1);
        }
        if (value < 0) {
            return "-" + getSimplifiedNumber(-value);
        }
        if (value < 1000) {
            return Long.toString(value); //deal with easy case
        }

        Map.Entry<Long, String> e = SUFFIXES.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static String addCommaAndRound(double amount) {
        amount = Math.round(amount);
        return FORMAT_COMMAS.format(amount);
    }

    public static String addCommaAndRoundHundredths(double amount) {
        return DECIMAL_FORMAT_COMMAS_OPTIONAL_HUNDREDTHS.format(amount);
    }

    public static String addCommas(double amount) {
        return FORMAT_COMMAS.format(amount);
    }
}
