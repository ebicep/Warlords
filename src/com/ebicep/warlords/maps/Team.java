package com.ebicep.warlords.maps;

import java.util.Arrays;
import java.util.Collections;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.Color;

public enum Team {
    RED(ChatColor.RED, "RED", Color.fromRGB(153, 51, 51)),
    BLUE(ChatColor.BLUE, "BLU", Color.fromRGB(51, 76, 178)),
    ;
    private final static Team[] inverseMapping;
    static {
        inverseMapping = values();
        Collections.reverse(Arrays.<Team>asList(inverseMapping));
    }
    private final ChatColor teamColor;
    private final String chatTag;
    private final String chatTagColored;
    private final String chatTagBoldColored;
    private final Color armorColor;

    private Team(ChatColor teamColor, String chatTag, Color armorColor) {
        this.teamColor = teamColor;
        this.chatTag = chatTag;
        this.chatTagColored = teamColor + chatTag;
        this.chatTagBoldColored = teamColor.toString() + ChatColor.BOLD + chatTag;
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
    public String prefix() {
        return chatTag;
    }

    @Nonnull
    public String coloredPrefix() {
        return chatTagColored;
    }

    @Nonnull
    public String boldColoredPrefix() {
        return chatTagBoldColored;
    }

    @Nonnull
    public Team enemy() {
        return inverseMapping[ordinal()];
    }

}
