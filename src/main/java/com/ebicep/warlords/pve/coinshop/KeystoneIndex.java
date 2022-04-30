package com.ebicep.warlords.pve.coinshop;

import javax.annotation.Nonnull;

public enum KeystoneIndex {

    CUTTING_EDGE("Cutting Edge"),
    BLEED("Bleed"),
    EMPOWERED_HARNESS("Empowered Harness"),
    DEFY("Defy"),
    CHAOS("Chaos"),
    REVENGE("Revenge"),
    WARHEAD("Warhead"),

    ;

    private final String name;

    KeystoneIndex(@Nonnull String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
