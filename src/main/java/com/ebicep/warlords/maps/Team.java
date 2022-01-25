package com.ebicep.warlords.maps;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;

public enum Team {
    BLUE("Blue", "BLU", ChatColor.BLUE, Color.fromRGB(51, 76, 178), new ItemStack(Material.WOOL, 1, (short) 11)),
    RED("Red", "RED", ChatColor.RED, Color.fromRGB(153, 51, 51), new ItemStack(Material.WOOL, 1, (short) 14)),
    ;
    private final static Team[] inverseMapping;

    static {
        inverseMapping = values();
        Collections.reverse(Arrays.asList(inverseMapping));
    }

    public final String name;
    private final ChatColor teamColor;
    private final String chatTag;
    private final String chatTagColored;
    private final String chatTagBoldColored;
    private final Color armorColor;
    public ItemStack item;

    Team(String name, String chatTag, ChatColor teamColor, Color armorColor, ItemStack item) {
        this.name = name;
        this.teamColor = teamColor;
        this.chatTag = chatTag;
        this.chatTagColored = teamColor + chatTag;
        this.chatTagBoldColored = teamColor.toString() + ChatColor.BOLD + chatTag;
        this.armorColor = armorColor;
        this.item = item;
    }

    @Nonnull
    public ChatColor teamColor() {
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
     * @return ChatColor.XXX + "XXX"
     */
    @Nonnull
    public String coloredPrefix() {
        return chatTagColored;
    }

    /**
     * Returns the prefix as ChatColor.XXX + ChatColor.BOLD + "XXX"
     * @return ChatColor.XXX + ChatColor.BOLD + "XXX"
     */
    @Nonnull
    public String boldColoredPrefix() {
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

}
