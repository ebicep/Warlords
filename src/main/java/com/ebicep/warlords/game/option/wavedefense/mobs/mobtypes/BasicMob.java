package com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes;

import com.ebicep.warlords.game.option.wavedefense.mobs.Mob;

public interface BasicMob extends Mob {

    @Override
    default int dropRate() {
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
