package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

public interface PassiveCooldown {

    int getTickCooldown();

    default int getSecondCooldown() {
        return getTickCooldown() / 20;
    }

}
