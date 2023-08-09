package com.ebicep.warlords.util.bukkit;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Colors {

    // https://prnt.sc/UN80GeSpeyly
    BLACK(NamedTextColor.BLACK, new ItemStack(Material.BLACK_WOOL)),
    DARK_BLUE(NamedTextColor.DARK_BLUE, new ItemStack(Material.BLUE_WOOL)),
    DARK_GREEN(NamedTextColor.DARK_GREEN, new ItemStack(Material.GREEN_WOOL)),
    DARK_AQUA(NamedTextColor.DARK_AQUA, new ItemStack(Material.LIGHT_BLUE_WOOL)),
    DARK_RED(NamedTextColor.DARK_RED, new ItemStack(Material.RED_WOOL)),
    DARK_PURPLE(NamedTextColor.DARK_PURPLE, new ItemStack(Material.PURPLE_WOOL)),
    GOLD(NamedTextColor.GOLD, new ItemStack(Material.ORANGE_WOOL)),
    GRAY(NamedTextColor.GRAY, new ItemStack(Material.LIGHT_GRAY_WOOL)),
    DARK_GRAY(NamedTextColor.DARK_GRAY, new ItemStack(Material.GRAY_WOOL)),
    BLUE(NamedTextColor.BLUE, new ItemStack(Material.LIGHT_BLUE_WOOL)),
    GREEN(NamedTextColor.GREEN, new ItemStack(Material.LIME_WOOL)),
    AQUA(NamedTextColor.AQUA, new ItemStack(Material.CYAN_WOOL)),
    RED(NamedTextColor.RED, new ItemStack(Material.RED_WOOL)),
    LIGHT_PURPLE(NamedTextColor.LIGHT_PURPLE, new ItemStack(Material.PINK_WOOL)),
    YELLOW(NamedTextColor.YELLOW, new ItemStack(Material.YELLOW_WOOL)),
    WHITE(NamedTextColor.WHITE, new ItemStack(Material.WHITE_WOOL));

    public final NamedTextColor textColor;
    public final ItemStack wool;

    Colors(NamedTextColor textColor, ItemStack wool) {
        this.textColor = textColor;
        this.wool = wool;
    }

}
