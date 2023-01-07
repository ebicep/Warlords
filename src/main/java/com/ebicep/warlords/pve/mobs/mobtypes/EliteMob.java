package com.ebicep.warlords.pve.mobs.mobtypes;

import com.ebicep.warlords.pve.mobs.Mob;

public interface EliteMob extends Mob {

    @Override
    default double dropRate() {
        return 5;
    }

    @Override
    default int commonDropChance() {
        return 75;
    }

    @Override
    default int rareDropChance() {
        return 20;
    }

    @Override
    default int epicDropChance() {
        return 5;
    }

}
