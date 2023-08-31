package com.ebicep.warlords.pve.mobs.tiers;

public interface CaptainMob extends Mob {

    @Override
    default double weaponDropRate() {
        return 5;
    }

    @Override
    default int commonWeaponDropChance() {
        return 70;
    }

    @Override
    default int rareWeaponDropChance() {
        return 18;
    }

    @Override
    default int epicWeaponDropChance() {
        return 7;
    }
}
