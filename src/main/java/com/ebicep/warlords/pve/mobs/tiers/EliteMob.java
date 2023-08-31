package com.ebicep.warlords.pve.mobs.tiers;

public interface EliteMob extends Mob {

    @Override
    default double weaponDropRate() {
        return 4;
    }

    @Override
    default int commonWeaponDropChance() {
        return 75;
    }

    @Override
    default int rareWeaponDropChance() {
        return 15;
    }

    @Override
    default int epicWeaponDropChance() {
        return 6;
    }

}
