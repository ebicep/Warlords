package com.ebicep.warlords.database.repositories.games;

public enum GamesCollections {

    ALL("All", "Games_Information"),
    CTF("Capture the Flag", "Games_Information_CTF"),
    TDM("Team Deathmatch", "Games_Information_TDM"),
    INTERCEPTION("Interception", "Games_Information_Interception"),
    DUEL("Duel", "Games_Information_Duel"),


    ;

    public String name;
    public String collectionName;

    GamesCollections(String name, String collectionName) {
        this.name = name;
        this.collectionName = collectionName;
    }
}
