package com.ebicep.warlords.game.option.pve.onslaught;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum OnslaughtDifficulty {

    EASY(Component.text("EASY", NamedTextColor.GREEN)),
    MEDIUM(Component.text("MEDIUM", NamedTextColor.YELLOW)),
    HARD(Component.text("HARD", NamedTextColor.GOLD)),
    INSANE(Component.text("INSANE", NamedTextColor.RED)),
    EXTREME(Component.text("EXTREME", NamedTextColor.DARK_RED)),
    NIGHTMARE(Component.text("NIGHTMARE", NamedTextColor.LIGHT_PURPLE)),
    INSOMNIA(Component.text("INSOMNIA", NamedTextColor.DARK_PURPLE)),
    VANGUARD(Component.text("VANGUARD", NamedTextColor.DARK_GRAY)),
    MAX(Component.text("?????", NamedTextColor.BLACK, TextDecoration.OBFUSCATED));

    private final TextComponent name;

    OnslaughtDifficulty(TextComponent name) {
        this.name = name;
    }

    public TextComponent getName() {
        return name;
    }
}
