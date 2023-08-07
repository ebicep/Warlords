package com.ebicep.warlords.pve.mobs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum MobTier {

    BASE(Component.text("", NamedTextColor.YELLOW)),
    ILLUSION(Component.text("Illusion", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)),
    ENVOY(Component.text("Envoy", NamedTextColor.GOLD, TextDecoration.BOLD)),
    VOID(Component.text("Void", NamedTextColor.DARK_GRAY, TextDecoration.BOLD)),
    EXILED(Component.text("Exiled", NamedTextColor.RED, TextDecoration.BOLD)),
    FORGOTTEN(Component.text("Forgotten", NamedTextColor.GRAY, TextDecoration.BOLD)),
    OVERGROWN(Component.text("Overgrown", NamedTextColor.GREEN, TextDecoration.BOLD)),

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
