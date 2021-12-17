package com.ebicep.warlords.database.repositories.player;

public enum PlayersCollections {

    ALL_TIME("Players_Information", "playersAllTime"),
    SEASON("Players_Information_Season", "playersSeason"),
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
