package com.ebicep.warlords.database.repositories.games;

public enum GamesCollections {

    ALL("All", "Games_Information"),
    CTF("Capture the Flag", "Games_Information_CTF"),
    TDM("Team Deathmatch", "Games_Information_TDM"),
    INTERCEPTION("Interception", "Games_Information_Interception"),
    DUEL("Duel", "Games_Information_Duel"),
    PVE("PvE", "Games_Information_PvE"),
//    TEMP("TEMP1", "TEMP1"),
//    TEMP2("TEMP2", "TEMP2"),
//    TEMP3("TEMP3", "TEMP3"),
//    TEMP4("TEMP4", "TEMP4"),


    ;

    public final String name;
    public final String collectionName;

    GamesCollections(String name, String collectionName) {
        this.name = name;
        this.collectionName = collectionName;
    }
}
