package com.ebicep.warlords.util.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Colors {

    // https://prnt.sc/UN80GeSpeyly
    BLACK(ChatColor.BLACK, new ItemStack(Material.BLACK_WOOL)),
    DARK_BLUE(ChatColor.DARK_BLUE, new ItemStack(Material.BLUE_WOOL)),
    DARK_GREEN(ChatColor.DARK_GREEN, new ItemStack(Material.GREEN_WOOL)),
    DARK_AQUA(ChatColor.DARK_AQUA, new ItemStack(Material.LIGHT_BLUE_WOOL)),
    DARK_RED(ChatColor.DARK_RED, new ItemStack(Material.RED_WOOL)),
    DARK_PURPLE(ChatColor.DARK_PURPLE, new ItemStack(Material.PURPLE_WOOL)),
    GOLD(ChatColor.GOLD, new ItemStack(Material.ORANGE_WOOL)),
    GRAY(ChatColor.GRAY, new ItemStack(Material.LIGHT_GRAY_WOOL)),
    DARK_GRAY(ChatColor.DARK_GRAY, new ItemStack(Material.GRAY_WOOL)),
    BLUE(ChatColor.BLUE, new ItemStack(Material.LIGHT_BLUE_WOOL)),
    GREEN(ChatColor.GREEN, new ItemStack(Material.LIME_WOOL)),
    AQUA(ChatColor.AQUA, new ItemStack(Material.CYAN_WOOL)),
    RED(ChatColor.RED, new ItemStack(Material.RED_WOOL)),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, new ItemStack(Material.PINK_WOOL)),
    YELLOW(ChatColor.YELLOW, new ItemStack(Material.YELLOW_WOOL)),
    WHITE(ChatColor.WHITE, new ItemStack(Material.WHITE_WOOL));

    public final ChatColor chatColor;
    public final ItemStack wool;

    Colors(ChatColor chatColor, ItemStack wool) {
        this.chatColor = chatColor;
        this.wool = wool;
    }

}
