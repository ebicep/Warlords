package com.ebicep.warlords.pve.mobs.mobtypes;

import com.ebicep.warlords.pve.mobs.Mob;

public interface BasicMob extends Mob {

    @Override
    default double dropRate() {
        return 1;
    }

    @Override
    default int commonDropChance() {
        return 90;
    }

    @Override
    default int rareDropChance() {
        return 9;
    }

    @Override
    default int epicDropChance() {
        return 1;
    }


}
