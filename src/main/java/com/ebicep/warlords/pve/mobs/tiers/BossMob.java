package com.ebicep.warlords.pve.mobs.tiers;

import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.mobs.flags.Unexecutable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

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
    default int getInternalLevel() {
        return 7;
    }

    @Override
    default Component getNamePrefix() {
        return Component.text("❂ BOSS ❂", NamedTextColor.DARK_RED, TextDecoration.BOLD);
    }
}
