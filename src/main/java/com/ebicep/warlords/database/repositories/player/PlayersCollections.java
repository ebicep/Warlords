package com.ebicep.warlords.database.repositories.player;

import java.util.Arrays;
import java.util.List;

public enum PlayersCollections {

    LIFETIME("Lifetime", "Players_Information", "playersAllTime"),
    SEASON_6("Season 6", "Players_Information_Season_6", "playersSeason6"),
    SEASON_5("Season 5", "Players_Information_Season_5", "playersSeason5"),
    SEASON_4("Season 4", "Players_Information_Season_4", "playersSeason4"),
    WEEKLY("Weekly", "Players_Information_Weekly", "playersWeekly"),
    DAILY("Daily", "Players_Information_Daily", "playersDaily"),
    //TEMP2("TEMP2", "TEMP2", "TEMP2"),

    ;

    public final String name;
    public final String collectionName;
    public final String cacheName;

    PlayersCollections(String name, String collectionName, String cacheName) {
        this.name = name;
        this.collectionName = collectionName;
        this.cacheName = cacheName;
    }

    //publiPlayersCollections[] values = PlayersCollections.values();


    public static List<PlayersCollections> getActiveCollections() {
        return Arrays.asList(LIFETIME, SEASON_6, WEEKLY, DAILY);
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
}
