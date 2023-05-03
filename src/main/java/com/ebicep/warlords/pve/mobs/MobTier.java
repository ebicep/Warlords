package com.ebicep.warlords.pve.mobs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum MobTier {

    BASE(Component.text("✻", NamedTextColor.YELLOW)),
    ELITE(Component.text("❈❈", NamedTextColor.GOLD, TextDecoration.BOLD)),
    BOSS(Component.text("✪✪✪", NamedTextColor.DARK_RED, TextDecoration.BOLD)),
    RAID_BOSS(Component.text("❂❂❂❂", NamedTextColor.DARK_GRAY, TextDecoration.BOLD));

    private final Component symbol;

    MobTier(Component symbol) {
        this.symbol = symbol;
    }

    public Component getSymbol() {
        return symbol;
    }

}
