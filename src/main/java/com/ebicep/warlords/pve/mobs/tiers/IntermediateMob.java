package com.ebicep.warlords.pve.mobs.tiers;

import net.kyori.adventure.text.Component;
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
    default int getInternalLevel() {
        return 2;
    }

    @Override
    default Component getNamePrefix() {
        return Component.text("INT", NamedTextColor.LIGHT_PURPLE);
    }

}
