package com.ebicep.warlords.pve.mobs.tiers;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public interface IntermediateMob extends Mob {

    @Override
    default double weaponDropRate() {
        return 2;
    }

    @Override
    default int commonWeaponDropChance() {
        return 85;
    }

    @Override
    default int rareWeaponDropChance() {
        return 10;
    }

    @Override
    default int epicWeaponDropChance() {
        return 3;
    }

    @Override
    default int getLevel() {
        return 2;
    }

    @Override
    default TextColor getTextColor() {
        return NamedTextColor.GOLD;
    }

}
