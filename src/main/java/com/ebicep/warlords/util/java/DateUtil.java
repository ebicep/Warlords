package com.ebicep.warlords.util.java;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class DateUtil {


    /**
     * @return Todays date at 10 AM UTC - Server restart time is 10 AM UTC
     */
    public static Instant getResetDateToday() {
        return OffsetDateTime
                .now(ZoneOffset.UTC)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .toInstant();
    }

    public static Instant getResetDateLatestMonday() {
        return OffsetDateTime
                .now(ZoneOffset.UTC)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .toInstant();
    }

    public static ZonedDateTime getCurrentDateEST() {
        return ZonedDateTime.now(ZoneId.of("America/New_York"));
    }

    public static String formatCurrentDateEST(String format) {
        return DateTimeFormatter.ofPattern(format).format(getCurrentDateEST());
    }

}
