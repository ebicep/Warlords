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
                    Criteria.where("plays").gt(20),
                    Criteria.where("pve_stats.plays").gt(20)
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
                    Criteria.where("plays").gt(30),
                    Criteria.where("pve_stats.plays").gt(30)
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
    public static final List<PlayersCollections> ACTIVE_LEADERBOARD_COLLECTIONS = Arrays.asList(LIFETIME, WEEKLY, DAILY);

    public static PlayersCollections getAfterCollection(PlayersCollections playersCollections) {
        int index = ACTIVE_LEADERBOARD_COLLECTIONS.indexOf(playersCollections);
        if (index == ACTIVE_LEADERBOARD_COLLECTIONS.size() - 1) {
            return ACTIVE_LEADERBOARD_COLLECTIONS.get(0);
        } else {
            return ACTIVE_LEADERBOARD_COLLECTIONS.get(index + 1);
        }

    }

    public static PlayersCollections getBeforeCollection(PlayersCollections playersCollections) {
        int index = ACTIVE_LEADERBOARD_COLLECTIONS.indexOf(playersCollections);
        if (index == 0) {
            return ACTIVE_LEADERBOARD_COLLECTIONS.get(ACTIVE_LEADERBOARD_COLLECTIONS.size() - 1);
        } else {
            return ACTIVE_LEADERBOARD_COLLECTIONS.get(index - 1);
        }
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
