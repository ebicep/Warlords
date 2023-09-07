package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public enum Aspect {

    ARMOURED("Armoured", NamedTextColor.DARK_GRAY),
    BLIZZARD("Blizzard", NamedTextColor.AQUA),
    EVASIVE("Evasive", NamedTextColor.GRAY), //TODO lighter gray?
    INFERNAL("Infernal", NamedTextColor.RED),
    JUGGERNAUT("Juggernaut", NamedTextColor.GOLD),
    REGENERATIVE("Regenerative", NamedTextColor.GREEN),
    SWIFT("Swift", NamedTextColor.BLUE),
    VAMPIRIC("Vampiric", NamedTextColor.DARK_RED),

    ;

    public static final Aspect[] VALUES = values();

    public final String name;
    public final TextColor textColor;

    Aspect(String name, TextColor textColor) {
        this.name = name;
        this.textColor = textColor;
    }

    public void apply(WarlordsEntity warlordsEntity) {

    }

}
