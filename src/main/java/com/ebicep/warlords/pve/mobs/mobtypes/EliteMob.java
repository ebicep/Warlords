package com.ebicep.warlords.pve.mobs.mobtypes;

import com.ebicep.warlords.pve.mobs.Mob;

public interface EliteMob extends Mob {

    @Override
    default double weaponDropRate() {
        return 5;
    }

    @Override
    default int commonWeaponDropChance() {
        return 75;
    }

    @Override
    default int rareWeaponDropChance() {
        return 20;
    }

    @Override
    default int epicWeaponDropChance() {
        return 5;
    }

}
