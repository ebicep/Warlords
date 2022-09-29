package com.ebicep.warlords.guilds;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.BiFunction;

public class GuildPlayerMuteEntry {

    private Instant start = Instant.now();
    private Instant end;
    @Field("time_unit")
    private TimeUnit timeUnit;
    private Integer duration;

    public GuildPlayerMuteEntry() {
        this.timeUnit = TimeUnit.PERMANENT;
    }

    public GuildPlayerMuteEntry(TimeUnit timeUnit, Integer duration) {
        this.timeUnit = timeUnit;
        this.duration = duration;
        this.end = timeUnit.instantFunction.apply(start, duration);
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public Integer getDuration() {
        return duration;
    }

    public Instant getEnd() {
        return end;
    }

    public enum TimeUnit {

        PERMANENT("Permanent", null, (instant, integer) -> null, 0),
        WEEKS("Week", "Weekly", (instant, integer) -> instant.plus(integer * 7L, ChronoUnit.DAYS), 4),
        DAYS("Day", "Daily", (instant, integer) -> instant.plus(integer, ChronoUnit.DAYS), 7),
        HOURS("Hour", "Hourly", (instant, integer) -> instant.plus(integer, ChronoUnit.HOURS), 24),
        MINUTES("Minute", "Minute", (instant, integer) -> instant.plus(integer, ChronoUnit.MINUTES), 60),

        ;

        public final String name;
        public final String lyName;
        public final BiFunction<Instant, Integer, Instant> instantFunction;
        public final int maxAmount;

        TimeUnit(String name, String lyName, BiFunction<Instant, Integer, Instant> instantFunction, int maxAmount) {
            this.name = name;
            this.lyName = lyName;
            this.instantFunction = instantFunction;
            this.maxAmount = maxAmount;
        }
    }

}
