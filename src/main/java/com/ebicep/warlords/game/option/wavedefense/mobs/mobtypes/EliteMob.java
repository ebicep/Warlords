package com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes;

import com.ebicep.warlords.game.option.wavedefense.mobs.Mob;

public interface EliteMob extends Mob {

    @Override
    default int dropRate() {
        return 5;
    }

    @Override
    default int commonDropChance() {
        return 75;
    }

    @Override
    default int rareDropChance() {
        return 25;
    }

    @Override
    default int epicDropChance() {
        return 5;
    }

}
