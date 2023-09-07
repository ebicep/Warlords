package com.ebicep.warlords.pve.mobs.tiers;

import com.ebicep.warlords.pve.mobs.mobflags.BossLike;
import com.ebicep.warlords.pve.mobs.mobflags.Unexecutable;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public interface BossMob extends Mob, Unexecutable, BossLike {

    @Override
    default double weaponDropRate() {
        return 10;
    }

    @Override
    default int commonWeaponDropChance() {
        return 50;
    }

    @Override
    default int rareWeaponDropChance() {
        return 25;
    }

    @Override
    default int epicWeaponDropChance() {
        return 15;
    }

    @Override
    default int getLevel() {
        return 7;
    }

    @Override
    default TextColor getTextColor() {
        return NamedTextColor.DARK_RED;
    }

}
