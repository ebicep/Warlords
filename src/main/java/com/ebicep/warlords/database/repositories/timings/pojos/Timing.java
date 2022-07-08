package com.ebicep.warlords.database.repositories.timings.pojos;

public enum Timing {

    WEEKLY(10080, 604800),
    DAILY(1440, 86400),

    ;

    public final long minuteDuration;
    public final long secondDuration;

    Timing(long minuteDuration, long secondDuration) {
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
