package com.ebicep.warlords.pve.mobs.tiers;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

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

    @Override
    default int getLevel() {
        return 3;
    }

    @Override
    default TextColor getTextColor() {
        return NamedTextColor.GREEN;
    }

}
