package com.ebicep.warlords.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;

public enum Team {
    BLUE(
            "Blue",
            "BLU",
            NamedTextColor.BLUE,
            ChatColor.BLUE,
            Color.fromRGB(51, 76, 178),
            new ItemStack(Material.BLUE_WOOL),
            new ItemStack(Material.BLUE_STAINED_GLASS)
    ),
    RED(
            "Red",
            "RED",
            NamedTextColor.RED,
            ChatColor.RED,
            Color.fromRGB(153, 51, 51),
            new ItemStack(Material.RED_WOOL),
            new ItemStack(Material.RED_STAINED_GLASS)
    ),

    ;
    private static final Team[] inverseMapping;

    static {
        inverseMapping = values();
        Collections.reverse(Arrays.asList(inverseMapping));
    }

    public final String name;
    public final NamedTextColor teamColor;
    @Deprecated
    public final ChatColor oldTeamColor;
    public final String chatTag;
    public final Component chatTagColored;
    public final Component chatTagBoldColored;
    public final Color armorColor;
    public final ItemStack woolItem;
    public final ItemStack glassItem;

    Team(String name, String chatTag, NamedTextColor teamColor, ChatColor oldTeamColor, Color armorColor, ItemStack woolItem, ItemStack glassItem) {
        this.name = name;
        this.teamColor = teamColor;
        this.chatTag = chatTag;
        this.oldTeamColor = oldTeamColor;
        this.glassItem = glassItem;
        this.chatTagColored = Component.text(chatTag, teamColor);
        this.chatTagBoldColored = Component.text(chatTag, teamColor, TextDecoration.BOLD);
        this.armorColor = armorColor;
        this.woolItem = woolItem;
    }

    @Nonnull
    public NamedTextColor teamColor() {
        return teamColor;
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

    /**
     * The team this team considers an enemy. 
     * @deprecated Because this method makes it hard to support more than 2 teams
     * @return the other team
     */
    @Deprecated
    @Nonnull
    public Team enemy() {
        return inverseMapping[ordinal()];
    }

    @Nonnull
    public ItemStack getWoolItem() {
        return woolItem;
    }

    /**
     * Returns the full team name. Examples: "Blue", "Red"
     * @return the full team name
     */
    @Nonnull
    public String getName() {
        return name;
    }

}
