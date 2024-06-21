package com.ebicep.warlords.game;

import com.ebicep.warlords.util.bukkit.Colors;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public enum Team {

    BLUE(
            Colors.DARK_BLUE,
            "Blue",
            "BLU",
            Color.fromRGB(51, 76, 178),
            BossBar.Color.BLUE
    ),
    RED(
            Colors.RED,
            "Red",
            "RED",
            Color.fromRGB(153, 51, 51),
            BossBar.Color.RED
    ),
    GAME(
            Colors.BLACK,
            "Game",
            "GAME",
            Color.fromRGB(0, 0, 0),
            BossBar.Color.WHITE
    ),

    ;

    public final String name;
    private final Colors colors;
    private final String chatTag;
    private final Component chatTagColored;
    private final Component chatTagBoldColored;
    private final Color armorColor;
    private final BossBar.Color bossBarColor;

    Team(Colors colors, String name, String chatTag, Color armorColor, BossBar.Color bossBarColor) {
        this.name = name;
        this.colors = colors;
        this.chatTag = chatTag;
        this.bossBarColor = bossBarColor;
        this.chatTagColored = Component.text(chatTag, colors.textColor);
        this.chatTagBoldColored = Component.text(chatTag, colors.textColor, TextDecoration.BOLD);
        this.armorColor = armorColor;
    }

    public Colors getColors() {
        return colors;
    }

    @Nonnull
    public Color armorColor() {
        return armorColor;
    }

    /**
     * Returns the prefix as "XXX" (typically 3 chars, all uppercase
     * @return "XXX"
     */
    @Nonnull
    public String prefix() {
        return chatTag;
    }

    /**
     * Returns the prefix as ChatColor.XXX + "XXX"
     *
     * @return ChatColor.XXX + "XXX"
     */
    @Nonnull
    public Component coloredPrefix() {
        return chatTagColored;
    }

    /**
     * Returns the prefix as ChatColor.XXX + ChatColor.BOLD + "XXX"
     *
     * @return ChatColor.XXX + ChatColor.BOLD + "XXX"
     */
    @Nonnull
    public Component boldColoredPrefix() {
        return chatTagBoldColored;
    }

    @Nonnull
    public Material getWool() {
        return colors.wool;
    }

    /**
     * Returns the full team name. Examples: "Blue", "Red"
     *
     * @return the full team name
     */
    @Nonnull
    public String getName() {
        return name;
    }

    public NamedTextColor getTeamColor() {
        return colors.textColor;
    }

    @Deprecated
    public ChatColor getChatColor() {
        return colors.chatColor;
    }

    public String getChatTag() {
        return chatTag;
    }

    public Component getChatTagColored() {
        return chatTagColored;
    }

    public Component getChatTagBoldColored() {
        return chatTagBoldColored;
    }

    public Color getArmorColor() {
        return armorColor;
    }

    public Material getGlass() {
        return colors.glass;
    }

    public BossBar.Color getBossBarColor() {
        return bossBarColor;
    }
}
