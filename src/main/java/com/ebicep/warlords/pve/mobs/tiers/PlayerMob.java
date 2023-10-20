package com.ebicep.warlords.pve.mobs.tiers;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public interface PlayerMob extends Mob {

    @Override
    default double weaponDropRate() {
        return 0;
    }

    @Override
    default int commonWeaponDropChance() {
        return 0;
    }

    @Override
    default int rareWeaponDropChance() {
        return 0;
    }

    @Override
    default int epicWeaponDropChance() {
        return 0;
    }

    @Override
    default int getLevel() {
        return 0;
    }

    @Override
    default TextColor getTextColor() {
        return NamedTextColor.AQUA;
    }

}
