package com.ebicep.warlords.maps;

public enum MapCategory {
    CAPTURE_THE_FLAG("CTF"),
    DEBUG("DEBUG"),
    OTHER("OTHER")

    ;

    private final String name;

    MapCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}