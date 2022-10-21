package com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes;

import com.ebicep.warlords.game.option.wavedefense.mobs.Mob;

public interface BossMob extends Mob {

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
