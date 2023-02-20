package com.ebicep.warlords.pve.items;

public enum ItemFamily {

    SPEED_BASIC("Speed"),
    ENERGY_BASIC("Energy"),
    POWER_BASIC("Power"),

    ;

    public final String name;

    ItemFamily(String name) {
        this.name = name;
    }
}
