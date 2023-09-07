package com.ebicep.warlords.pve.mobs.tiers;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public interface ChampionMob extends Mob {

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

    @Override
    default int getLevel() {
        return 5;
    }


    @Override
    default TextColor getTextColor() {
        return NamedTextColor.BLUE;
    }

}
