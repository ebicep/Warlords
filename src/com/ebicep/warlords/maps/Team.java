package com.ebicep.warlords.maps;

import java.util.Arrays;
import java.util.Collections;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.Color;

public enum Team {
    RED(ChatColor.RED, Color.fromRGB(153, 51, 51)),
    BLUE(ChatColor.BLUE, Color.fromRGB(51, 76, 178)),
    ;
    private final static Team[] inverseMapping;
    static {
        inverseMapping = values();
        Collections.reverse(Arrays.<Team>asList(inverseMapping));
    }
    private final ChatColor teamColor;
    private final Color armorColor;

    private Team(ChatColor teamColor, Color armorColor) {
        this.teamColor = teamColor;
        this.armorColor = armorColor;
    }

    @Nonnull
    public ChatColor teamColor() {
        return teamColor;
    }

    @Nonnull
    public Color armorColor() {
        return armorColor;
    }

    @Nonnull
    public Team enemy() {
        return inverseMapping[ordinal()];
    }

}