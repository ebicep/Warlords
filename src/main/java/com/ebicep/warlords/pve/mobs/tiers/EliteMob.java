package com.ebicep.warlords.pve.mobs.tiers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

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

    @Override
    default int getInternalLevel() {
        return 4;
    }


    @Override
    default Component getNamePrefix() {
        return Component.text("ELITE", NamedTextColor.DARK_PURPLE);
    }

}
