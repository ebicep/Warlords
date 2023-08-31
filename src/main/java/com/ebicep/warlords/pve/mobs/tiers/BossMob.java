package com.ebicep.warlords.pve.mobs.tiers;

public interface BossMob extends Mob {

    @Override
    default double weaponDropRate() {
        return 10;
    }

    @Override
    default int commonWeaponDropChance() {
        return 50;
    }

    @Override
    default int rareWeaponDropChance() {
        return 25;
    }

    @Override
    default int epicWeaponDropChance() {
        return 15;
    }
}
