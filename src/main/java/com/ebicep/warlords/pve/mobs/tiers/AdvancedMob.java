package com.ebicep.warlords.pve.mobs.tiers;

public interface AdvancedMob extends Mob {

    @Override
    default double weaponDropRate() {
        return 3;
    }

    @Override
    default int commonWeaponDropChance() {
        return 80;
    }

    @Override
    default int rareWeaponDropChance() {
        return 12;
    }

    @Override
    default int epicWeaponDropChance() {
        return 5;
    }
}
