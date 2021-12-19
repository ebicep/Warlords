package com.ebicep.warlords.database.repositories.player;

public enum PlayersCollections {

    ALL_TIME("Players_Information", "playersAllTime"),
    SEASON_5("Players_Information_Season_5", "playersSeason5"),
    SEASON_4("Players_Information_Season_4", "playersSeason4"),
    WEEKLY("Players_Information_Weekly", "playersWeekly"),
    DAILY("Players_Information_Daily", "playersDaily"),

    ;

    public String collectionName;
    public String cacheName;

    PlayersCollections(String collectionName, String cacheName) {
        this.collectionName = collectionName;
        this.cacheName = cacheName;
    }
}
