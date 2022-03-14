package com.ebicep.warlords.util.java;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class NumberFormat {

    public static final DecimalFormat decimalFormatOptionalHundredths = new DecimalFormat("#.##");
    static final DecimalFormat decimalFormatOptionalTenths = new DecimalFormat("#.#");
    static final DecimalFormat decimalFormatTenths = new DecimalFormat("0.0");
    static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        decimalFormatOptionalTenths.setDecimalSeparatorAlwaysShown(false);
        decimalFormatTenths.setDecimalSeparatorAlwaysShown(false);
        decimalFormatOptionalHundredths.setDecimalSeparatorAlwaysShown(false);

        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "m");
        suffixes.put(1_000_000_000L, "b");
        suffixes.put(1_000_000_000_000L, "t");
        suffixes.put(1_000_000_000_000_000L, "p");
        suffixes.put(1_000_000_000_000_000_000L, "e");
    }

    public static String formatOptionalHundredths(double value) {
        return decimalFormatOptionalHundredths.format(value);
    }

    public static String formatOptionalTenths(double value) {
        return decimalFormatOptionalTenths.format(value);
    }

    public static String formatTenths(double value) {
        return NumberFormat.decimalFormatTenths.format(value);
    }

    public static String getSimplifiedNumber(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return getSimplifiedNumber(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + getSimplifiedNumber(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static String addCommaAndRound(double amount) {
        amount = Math.round(amount);
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }
}
