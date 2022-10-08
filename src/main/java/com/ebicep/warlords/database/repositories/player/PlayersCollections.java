package com.ebicep.warlords.database.repositories.player;

import com.ebicep.warlords.util.java.DateUtil;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;

public enum PlayersCollections {

    LIFETIME("Lifetime", "Players_Information", "playersAllTime") {
        @Override
        public boolean shouldUpdate(Instant dateOfGame) {
            return true;
        }
    },
    SEASON_6("Season 6", "Players_Information_Season_6", "playersSeason6") {
        @Override
        public boolean shouldUpdate(Instant dateOfGame) {
            return ACTIVE_COLLECTIONS.contains(this);
        }
    },
    SEASON_5("Season 5", "Players_Information_Season_5", "playersSeason5") {
        @Override
        public boolean shouldUpdate(Instant dateOfGame) {
            return ACTIVE_COLLECTIONS.contains(this);
        }
    },
    SEASON_4("Season 4", "Players_Information_Season_4", "playersSeason4") {
        @Override
        public boolean shouldUpdate(Instant dateOfGame) {
            return ACTIVE_COLLECTIONS.contains(this);
        }
    },
    WEEKLY("Weekly", "Players_Information_Weekly", "playersWeekly") {
        @Override
        public boolean shouldUpdate(Instant dateOfGame) {
            return OffsetDateTime
                    .now(ZoneOffset.UTC)
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0)
                    .toInstant()
                    .isBefore(dateOfGame);
        }
    },
    DAILY("Daily", "Players_Information_Daily", "playersDaily") {
        @Override
        public boolean shouldUpdate(Instant dateOfGame) {
            return DateUtil.getResetDateToday()
                    .isBefore(dateOfGame);
        }
    },
//    TEMP("TEMP1", "TEMP1", "TEMP1") {
//        @Override
//        public boolean shouldUpdate(Instant dateOfGame) {
//            return false;
//        }
//    },
//    TEMP2("TEMP2", "TEMP2", "TEMP2") {
//        @Override
//        public boolean shouldUpdate(Instant dateOfGame) {
//            return false;
//        }
//    },

    ;

    public static final PlayersCollections[] VALUES = values();
    public static final List<PlayersCollections> ACTIVE_COLLECTIONS = Arrays.asList(LIFETIME, SEASON_6, WEEKLY, DAILY);
    public final String name;
    public final String collectionName;
    public final String cacheName;

    PlayersCollections(String name, String collectionName, String cacheName) {
        this.name = name;
        this.collectionName = collectionName;
        this.cacheName = cacheName;
    }

    public static PlayersCollections getAfterCollection(PlayersCollections playersCollections) {
        switch (playersCollections) {
            case LIFETIME:
                return SEASON_6;
            case SEASON_6:
                return SEASON_5;
            case SEASON_5:
                return SEASON_4;
            case SEASON_4:
                return WEEKLY;
            case WEEKLY:
                return DAILY;
            case DAILY:
                return LIFETIME;
        }
        return LIFETIME;
    }

    public static PlayersCollections getBeforeCollection(PlayersCollections playersCollections) {
        switch (playersCollections) {
            case LIFETIME:
                return DAILY;
            case SEASON_6:
                return LIFETIME;
            case SEASON_5:
                return SEASON_6;
            case SEASON_4:
                return SEASON_5;
            case WEEKLY:
                return SEASON_4;
            case DAILY:
                return WEEKLY;
        }
        return LIFETIME;
    }

    public abstract boolean shouldUpdate(Instant dateOfGame);
}
