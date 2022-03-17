package com.ebicep.warlords.database.repositories.player;

import com.ebicep.warlords.database.leaderboards.sections.LeaderboardCategory;
import me.filoghost.holographicdisplays.api.beta.hologram.Hologram;

import java.util.List;
import java.util.function.Function;

public enum PlayersCollections {

    LIFETIME("Lifetime", "Players_Information", "playersAllTime", LeaderboardCategory::getLifeTimeHolograms),
    SEASON_6("Season 6", "Players_Information_Season_6", "playersSeason6", LeaderboardCategory::getSeason6Holograms),
    SEASON_5("Season 5", "Players_Information_Season_5", "playersSeason5", LeaderboardCategory::getSeason5Holograms),
    SEASON_4("Season 4", "Players_Information_Season_4", "playersSeason4", LeaderboardCategory::getSeason4Holograms),
    WEEKLY("Weekly", "Players_Information_Weekly", "playersWeekly", LeaderboardCategory::getWeeklyHolograms),
    DAILY("Daily", "Players_Information_Daily", "playersDaily", LeaderboardCategory::getDailyHolograms),
    //TEMP2("TEMP2", "TEMP2", "TEMP2", null),

    ;

    public String name;
    public String collectionName;
    public String cacheName;
    public Function<LeaderboardCategory<?>, List<Hologram>> function;

    PlayersCollections(String name, String collectionName, String cacheName, Function<LeaderboardCategory<?>, List<Hologram>> function) {
        this.name = name;
        this.collectionName = collectionName;
        this.cacheName = cacheName;
        this.function = function;
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
