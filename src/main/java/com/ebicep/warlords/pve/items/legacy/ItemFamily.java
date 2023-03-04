package com.ebicep.warlords.pve.items.legacy;

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
