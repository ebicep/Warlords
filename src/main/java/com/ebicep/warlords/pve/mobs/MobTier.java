package com.ebicep.warlords.pve.mobs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum MobTier {

    // TODO: interfaces

    BASE(Component.text("", NamedTextColor.YELLOW)),
    ILLUSION(Component.text("❅", NamedTextColor.DARK_PURPLE)),
    ENVOY(Component.text("✶", NamedTextColor.GOLD)),
    VOID(Component.text("❈", NamedTextColor.DARK_GRAY)),
    EXILED(Component.text("✮", NamedTextColor.RED)),
    FORGOTTEN(Component.text("✤", NamedTextColor.GRAY)),
    OVERGROWN(Component.text("✺", NamedTextColor.GREEN)),

    BOSS(Component.text("BOSS ✪", NamedTextColor.DARK_RED, TextDecoration.BOLD)),
    RAID_BOSS(Component.text("RAID CHAMPION ❂", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))

    ;

    private final Component symbol;

    MobTier(Component symbol) {
        this.symbol = symbol;
    }

    public Component getSymbol() {
        return symbol;
    }

}
