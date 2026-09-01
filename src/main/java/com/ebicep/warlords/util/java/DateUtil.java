package com.ebicep.warlords.util.java;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * Period boundaries for timed game features. All periods roll at <b>10:00 UTC</b> (server restart time).
 * Until that moment the server is still in the previous period.
 * <ul>
 *     <li>Daily — each day at 10 UTC</li>
 *     <li>Weekly — each Monday at 10 UTC</li>
 *     <li>Monthly — the 1st of each month at 10 UTC</li>
 * </ul>
 */
public class DateUtil {

    /**
     * @return Todays date at 10 AM UTC - Server restart time is 10 AM UTC
     */
    public static Instant getResetDateToday() {
        Instant instant = OffsetDateTime
                .now(ZoneOffset.UTC)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .toInstant();
        if (instant.isBefore(Instant.now())) {
            return instant;
        } else {
            return instant.minus(1, ChronoUnit.DAYS);
        }
    }

    /**
     * @return The next future 10 AM UTC (today if still ahead, otherwise tomorrow)
     */
    public static Instant getNextResetDate() {
        OffsetDateTime next = OffsetDateTime
                .now(ZoneOffset.UTC)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        if (!next.toInstant().isAfter(Instant.now())) {
            next = next.plusDays(1);
        }
        return next.toInstant();
    }

    /**
     * @return The most recent Monday at 10 AM UTC (start of the current weekly period)
     */
    public static Instant getResetDateCurrentWeek() {
        Instant instant = OffsetDateTime
                .now(ZoneOffset.UTC)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .toInstant();
        if (instant.isBefore(Instant.now())) {
            return instant;
        } else {
            return instant.minus(7, ChronoUnit.DAYS);
        }
    }

    /**
     * @return The next future Monday at 10 AM UTC
     */
    public static Instant getNextWeeklyResetDate() {
        OffsetDateTime next = OffsetDateTime
                .now(ZoneOffset.UTC)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        if (!next.toInstant().isAfter(Instant.now())) {
            next = next.plusWeeks(1);
        }
        return next.toInstant();
    }

    /**
     * @return The most recent Monday at 10 AM UTC (start of the current weekly period)
     */
    public static Instant getResetDateLatestMonday() {
        return getResetDateCurrentWeek();
    }

    /**
     * @return UTC epoch day of the current weekly period start ({@link #getResetDateCurrentWeek()})
     */
    public static long getCurrentWeekStartEpochDay() {
        return getResetDateCurrentWeek().atZone(ZoneOffset.UTC).toLocalDate().toEpochDay();
    }

    /**
     * @return The next future 1st of the month at 10 AM UTC
     */
    public static Instant getNextMonthlyResetDate() {
        OffsetDateTime next = OffsetDateTime
                .now(ZoneOffset.UTC)
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        if (!next.toInstant().isAfter(Instant.now())) {
            next = next.plusMonths(1);
        }
        return next.toInstant();
    }

    /**
     * @return The next future 1st of the month at 10 AM UTC
     * @see #getNextMonthlyResetDate()
     */
    public static Instant getNextMonthFirstDay() {
        return getNextMonthlyResetDate();
    }

    /**
     * @return The 1st of the current month at 10 AM UTC (start of the current monthly period)
     */
    public static Instant getResetDateCurrentMonth() {
        Instant instant = OffsetDateTime
                .now(ZoneOffset.UTC)
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .toInstant();
        if (instant.isBefore(Instant.now())) {
            return instant;
        } else {
            return instant.minus(1, ChronoUnit.MONTHS);
        }
    }

    public static String formatCurrentDateEST(String format) {
        return DateTimeFormatter.ofPattern(format).format(getCurrentDateEST());
    }

    public static ZonedDateTime getCurrentDateEST() {
        return ZonedDateTime.now(ZoneId.of("America/New_York"));
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
