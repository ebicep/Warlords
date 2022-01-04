package com.ebicep.warlords.database.repositories.games;

public enum GameMode {

    CAPTURE_THE_FLAG("Capture The Flag"),

    ;

    public String name;

    GameMode(String name) {
        this.name = name;
    }
}
