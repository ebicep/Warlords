package com.ebicep.warlords.pve.coinshop;

import javax.annotation.Nonnull;

public enum KeystoneIndex {

    CUTTING_EDGE("Cutting Edge", ""),
    BLEED("Bleed", ""),
    EMPOWERED_HARNESS("Empowered Harness", ""),
    DEFY("Defy", ""),
    CHAOS("Chaos", ""),
    REVENGE("Revenge", ""),
    WARHEAD("Warhead", ""),

    ;

    private final String name;
    private final String description;

    KeystoneIndex(@Nonnull String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
