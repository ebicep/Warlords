package com.ebicep.warlords.database.repositories.timings.pojos;

public enum Timing {

    LIFETIME("Lifetime", 0, 0),
    MONTHLY("Monthly", 0, 0),
    WEEKLY("Weekly", 10080, 604800),
    DAILY("Daily", 1440, 86400),

    ;

    public static final Timing[] VALUES = values();
    public final String name;
    public final long minuteDuration;
    public final long secondDuration;

    Timing(String name, long minuteDuration, long secondDuration) {
        this.name = name;
        this.minuteDuration = minuteDuration;
        this.secondDuration = secondDuration;
    }

    public static long millisecondToSecond(long milliSeconds) {
        return milliSeconds / 1000;
    }

    public static long secondToMinute(long seconds) {
        return seconds / 60;
    }

    public static long millisecondToMinute(long milliSeconds) {
        return secondToMinute(millisecondToSecond(milliSeconds));
    }


}
