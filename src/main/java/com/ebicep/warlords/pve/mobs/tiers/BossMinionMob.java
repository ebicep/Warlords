package com.ebicep.warlords.pve.mobs.tiers;

import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.mobs.flags.Unexecutable;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public interface BossMinionMob extends Mob, Unexecutable, BossLike {

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

    @Override
    default int getLevel() {
        return 6;
    }

    @Override
    default TextColor getTextColor() {
        return NamedTextColor.RED;
    }

}
