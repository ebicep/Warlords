package com.ebicep.warlords.database.repositories.player;

import com.ebicep.warlords.util.java.DateUtil;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;

public enum PlayersCollections {

    LIFETIME("Lifetime", "Players_Information") {
        @Override
        public boolean shouldUpdate(Instant dateOfGame) {
            return true;
        }

        @Override
        public Query getQuery() {
            return new Query(Criteria.where("last_login").gt(Instant.now().minus(10, ChronoUnit.DAYS)));
        }
    },
    MONTHLY("Monthly", "Players_Information_Monthly") {
        @Override
        public boolean shouldUpdate(Instant dateOfGame) {
            ZonedDateTime gameTime = dateOfGame.atZone(ZoneOffset.UTC);
            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
            return gameTime.getMonth() == now.getMonth() && gameTime.getYear() == now.getYear();
        }

        @Override
        public Query getQuery() {
            return new Query(new Criteria().orOperator(
                    Criteria.where("plays").gt(5),
                    Criteria.where("pve_stats.plays").gt(5)
            ));
        }
    },
    SEASON_8("Season 8", "Players_Information_Season_8") {
        @Override
        public boolean shouldUpdate(Instant dateOfGame) {
            return ACTIVE_COLLECTIONS.contains(this);
        }

        @Override
        public Query getQuery() {
            return new Query(new Criteria().orOperator(
                    Criteria.where("plays").gt(10),
                    Criteria.where("pve_stats.plays").gt(10)
            ));
        }
    },
//    SEASON_7("Season 7", "Players_Information_Season_7") {
//        @Override
//        public boolean shouldUpdate(Instant dateOfGame) {
//            return ACTIVE_COLLECTIONS.contains(this);
//        }
//    },
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
    public static final List<PlayersCollections> ACTIVE_COLLECTIONS = Arrays.asList(LIFETIME, MONTHLY, SEASON_8, WEEKLY, DAILY);

    public static PlayersCollections getAfterCollection(PlayersCollections playersCollections) {
        return switch (playersCollections) {
            case LIFETIME -> MONTHLY;
            case MONTHLY -> SEASON_8;
            case SEASON_8 -> WEEKLY;
//            case SEASON_5:
//                return SEASON_4;
//            case SEASON_4:
//                return WEEKLY;
            case WEEKLY -> DAILY;
            case DAILY -> LIFETIME;
        };
    }

    public static PlayersCollections getBeforeCollection(PlayersCollections playersCollections) {
        return switch (playersCollections) {
            case LIFETIME -> DAILY;
            case MONTHLY -> LIFETIME;
            case SEASON_8 -> MONTHLY;
//            case SEASON_5:
//                return SEASON_6;
//            case SEASON_4:
//                return SEASON_5;
            case WEEKLY -> SEASON_8;
            case DAILY -> WEEKLY;
        };
    }

    public final String name;
    public final String collectionName;

    PlayersCollections(String name, String collectionName) {
        this.name = name;
        this.collectionName = collectionName;
    }

    public abstract boolean shouldUpdate(Instant dateOfGame);

    public Query getQuery() {
        return new Query();
    }

}
