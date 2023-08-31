package com.ebicep.warlords.pve.mobs.tiers;

public interface BossMinionMob extends Mob {

    @Override
    default double weaponDropRate() {
        return 6;
    }

    @Override
    default int commonWeaponDropChance() {
        return 65;
    }

    @Override
    default int rareWeaponDropChance() {
        return 20;
    }

    @Override
    default int epicWeaponDropChance() {
        return 9;
    }
}
