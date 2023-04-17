package com.ebicep.warlords.database.repositories.player;

import com.ebicep.warlords.util.java.DateUtil;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;

public enum PlayersCollections {

    LIFETIME("Lifetime", "Players_Information") {
        @Override
        public boolean shouldUpdate(Instant dateOfGame) {
            return true;
        }
    },
    MONTHLY("Monthly", "Players_Information_Monthly") {
        @Override
        public boolean shouldUpdate(Instant dateOfGame) {
            ZonedDateTime gameTime = dateOfGame.atZone(ZoneOffset.UTC);
            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
            return gameTime.getMonth() == now.getMonth() && gameTime.getYear() == now.getYear();
        }
    },
    SEASON_7("Season 7", "Players_Information_Season_7") {
        @Override
        public boolean shouldUpdate(Instant dateOfGame) {
            return ACTIVE_COLLECTIONS.contains(this);
        }
    },
//        SEASON_6("Season 6", "Players_Information_Season_6") {
//        @Override
//        public boolean shouldUpdate(Instant dateOfGame) {
//            return ACTIVE_COLLECTIONS.contains(this);
//        }
//    },
//    SEASON_5("Season 5", "Players_Information_Season_5") {
//        @Override
//        public boolean shouldUpdate(Instant dateOfGame) {
//            return ACTIVE_COLLECTIONS.contains(this);
//        }
//    },
//    SEASON_4("Season 4", "Players_Information_Season_4") {
//        @Override
//        public boolean shouldUpdate(Instant dateOfGame) {
//            return ACTIVE_COLLECTIONS.contains(this);
//        }
//    },

    WEEKLY("Weekly", "Players_Information_Weekly") {
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
    DAILY("Daily", "Players_Information_Daily") {
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
    public static final List<PlayersCollections> ACTIVE_COLLECTIONS = Arrays.asList(LIFETIME, MONTHLY, SEASON_7, WEEKLY, DAILY);

    public static PlayersCollections getAfterCollection(PlayersCollections playersCollections) {
        switch (playersCollections) {
            case LIFETIME:
                return MONTHLY;
            case MONTHLY:
                return SEASON_7;
            case SEASON_7:
                return WEEKLY;
//            case SEASON_5:
//                return SEASON_4;
//            case SEASON_4:
//                return WEEKLY;
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
            case MONTHLY:
                return LIFETIME;
            case SEASON_7:
                return MONTHLY;
//            case SEASON_5:
//                return SEASON_6;
//            case SEASON_4:
//                return SEASON_5;
            case WEEKLY:
                return SEASON_7;
            case DAILY:
                return WEEKLY;
        }
        return LIFETIME;
    }

    public final String name;
    public final String collectionName;

    PlayersCollections(String name, String collectionName) {
        this.name = name;
        this.collectionName = collectionName;
    }

    public abstract boolean shouldUpdate(Instant dateOfGame);
}
