package com.ebicep.warlords.maps;

public enum MapCategory {
    CAPTURE_THE_FLAG("Capture The Flag"),
	INTERCEPTION("Interception"),
    DEBUG("Debug Map"),
    OTHER("PLACEHOLDER"),
    ;

    private final String name;

    MapCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
