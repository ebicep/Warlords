package com.ebicep.warlords.util.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Colors {

    // https://prnt.sc/UN80GeSpeyly
    BLACK(ChatColor.BLACK, new ItemStack(Material.WOOL, 1, (byte) 15)),
    DARK_BLUE(ChatColor.DARK_BLUE, new ItemStack(Material.WOOL, 1, (byte) 11)),
    DARK_GREEN(ChatColor.DARK_GREEN, new ItemStack(Material.WOOL, 1, (byte) 13)),
    DARK_AQUA(ChatColor.DARK_AQUA, new ItemStack(Material.WOOL, 1, (byte) 9)),
    DARK_RED(ChatColor.DARK_RED, new ItemStack(Material.WOOL, 1, (byte) 14)),
    DARK_PURPLE(ChatColor.DARK_PURPLE, new ItemStack(Material.WOOL, 1, (byte) 10)),
    GOLD(ChatColor.GOLD, new ItemStack(Material.WOOL, 1, (byte) 1)),
    GRAY(ChatColor.GRAY, new ItemStack(Material.WOOL, 1, (byte) 8)),
    DARK_GRAY(ChatColor.DARK_GRAY, new ItemStack(Material.WOOL, 1, (byte) 7)),
    BLUE(ChatColor.BLUE, new ItemStack(Material.WOOL, 1, (byte) 3)),
    GREEN(ChatColor.GREEN, new ItemStack(Material.WOOL, 1, (byte) 5)),
    AQUA(ChatColor.AQUA, new ItemStack(Material.WOOL, 1, (byte) 9)),
    RED(ChatColor.RED, new ItemStack(Material.WOOL, 1, (byte) 14)),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, new ItemStack(Material.WOOL, 1, (byte) 6)),
    YELLOW(ChatColor.YELLOW, new ItemStack(Material.WOOL, 1, (byte) 4)),
    WHITE(ChatColor.WHITE, new ItemStack(Material.WOOL, 1, (byte) 0));

    public final ChatColor chatColor;
    public final ItemStack wool;

    Colors(ChatColor chatColor, ItemStack wool) {
        this.chatColor = chatColor;
        this.wool = wool;
    }

}
