package com.ebicep.warlords.abilities.internal;

public interface CanReduceCooldowns {

    default boolean canReduceCooldowns() {
        return true;
    }

}
