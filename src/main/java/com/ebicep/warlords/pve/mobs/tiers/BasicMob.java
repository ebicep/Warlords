package com.ebicep.warlords.pve.mobs.tiers;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public interface BasicMob extends Mob {

    @Override
    default double weaponDropRate() {
        return 1;
    }

    @Override
    default int commonWeaponDropChance() {
        return 90;
    }

    @Override
    default int rareWeaponDropChance() {
        return 9;
    }

    @Override
    default int epicWeaponDropChance() {
        return 1;
    }

    @Override
    default int getLevel() {
        return 1;
    }

    @Override
    default TextColor getTextColor() {
        return NamedTextColor.YELLOW;
    }

}
