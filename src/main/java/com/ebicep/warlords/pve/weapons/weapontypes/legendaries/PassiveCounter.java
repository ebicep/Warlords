package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

public interface PassiveCounter {

    int getCounter();

    default boolean constantlyUpdate() {
        return true;
    }
}
