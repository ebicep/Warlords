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
                .withNano(0)
                .toInstant();
    }

    public static Instant getNextResetDate() {
        return OffsetDateTime
                .now(ZoneOffset.UTC)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .toInstant();
    }

    public static Instant getResetDateLatestMonday() {
        return OffsetDateTime
                .now(ZoneOffset.UTC)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .toInstant();
    }

    public static ZonedDateTime getCurrentDateEST() {
        return ZonedDateTime.now(ZoneId.of("America/New_York"));
    }

    public static String formatCurrentDateEST(String format) {
        return DateTimeFormatter.ofPattern(format).format(getCurrentDateEST());
    }

    public static String getTimeTill(
            Instant endDate,
            boolean includeDays,
            boolean includeHours,
            boolean includeMinutes,
            boolean includeSeconds
    ) {
        Duration duration = Duration.between(Instant.now(), endDate);

        String timeLeft = "";
        if (includeDays) {
            long days = duration.toDaysPart();
            if (days > 0) {
                timeLeft += days + (days == 1 ? " day " : " days ");
            }
        }
        if (includeHours) {
            long hours = duration.toHoursPart();
            if (hours > 0) {
                timeLeft += hours + (hours == 1 ? " hour " : " hours ");
            }
        }
        if (includeMinutes) {
            long minutes = duration.toMinutesPart();
            if (minutes > 0) {
                timeLeft += minutes + (minutes == 1 ? " minute " : " minutes ");
            }
        }
        if (includeSeconds) {
            long seconds = duration.toSecondsPart();
            if (seconds > 0) {
                timeLeft += seconds + (seconds == 1 ? " second " : " seconds ");
            }
        }

        if (timeLeft.isEmpty()) {
            return "0 seconds";
        } else {
            return timeLeft.substring(0, timeLeft.length() - 1);
        }
    }
}
