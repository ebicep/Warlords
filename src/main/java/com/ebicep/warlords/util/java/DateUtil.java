package com.ebicep.warlords.util.java;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    public static String getTimeTill(
            Instant endDate,
            boolean includeDays,
            boolean includeHours,
            boolean includeMinutes,
            boolean includeSeconds
    ) {
        Instant currentDate = Instant.now();

        String timeLeft = "";
        if (includeDays) {
            long days = ChronoUnit.DAYS.between(currentDate, endDate);
            if (days > 0) {
                timeLeft += days + (days == 1 ? " day " : " days ");
            }
        }
        if (includeHours) {
            long hours = ChronoUnit.HOURS.between(currentDate, endDate) % 24;
            if (hours > 0) {
                timeLeft += hours + (hours == 1 ? " hour " : " hours ");
            }
        }
        if (includeMinutes) {
            long minutes = ChronoUnit.MINUTES.between(currentDate, endDate) % 60;
            if (minutes > 0) {
                timeLeft += minutes + (minutes == 1 ? " minute " : " minutes ");
            }
        }
        if (includeSeconds) {
            long seconds = ChronoUnit.SECONDS.between(currentDate, endDate) % 60;
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
