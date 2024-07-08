package com.ebicep.warlords.util.bukkit;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

public enum Colors {

    // https://prnt.sc/UN80GeSpeyly
    BLACK(ChatColor.BLACK,
            NamedTextColor.BLACK,
            Material.BLACK_WOOL,
            Color.fromRGB(0, 0, 0),
            Material.BLACK_STAINED_GLASS,
            Material.BLACK_BANNER
    ),
    DARK_BLUE(ChatColor.DARK_BLUE,
            NamedTextColor.DARK_BLUE,
            Material.BLUE_WOOL,
            Color.fromRGB(0, 0, 170),
            Material.BLUE_STAINED_GLASS,
            Material.BLUE_BANNER
    ),
    DARK_GREEN(ChatColor.DARK_GREEN,
            NamedTextColor.DARK_GREEN,
            Material.GREEN_WOOL,
            Color.fromRGB(0, 170, 0),
            Material.GREEN_STAINED_GLASS,
            Material.GREEN_BANNER
    ),
    DARK_AQUA(ChatColor.DARK_AQUA,
            NamedTextColor.DARK_AQUA,
            Material.LIGHT_BLUE_WOOL,
            Color.fromRGB(0, 170, 170),
            Material.LIGHT_BLUE_STAINED_GLASS,
            Material.LIGHT_BLUE_BANNER
    ),
    DARK_RED(ChatColor.DARK_RED,
            NamedTextColor.DARK_RED,
            Material.RED_WOOL,
            Color.fromRGB(170, 0, 0),
            Material.RED_STAINED_GLASS,
            Material.RED_BANNER
    ),
    DARK_PURPLE(ChatColor.DARK_PURPLE,
            NamedTextColor.DARK_PURPLE,
            Material.PURPLE_WOOL,
            Color.fromRGB(170, 0, 170),
            Material.PURPLE_STAINED_GLASS,
            Material.PURPLE_BANNER
    ),
    GOLD(ChatColor.GOLD,
            NamedTextColor.GOLD,
            Material.ORANGE_WOOL,
            Color.fromRGB(255, 170, 0),
            Material.ORANGE_STAINED_GLASS,
            Material.ORANGE_BANNER
    ),
    GRAY(ChatColor.GRAY,
            NamedTextColor.GRAY,
            Material.LIGHT_GRAY_WOOL,
            Color.fromRGB(170, 170, 170),
            Material.LIGHT_GRAY_STAINED_GLASS,
            Material.LIGHT_GRAY_BANNER
    ),
    DARK_GRAY(ChatColor.DARK_GRAY,
            NamedTextColor.DARK_GRAY,
            Material.GRAY_WOOL,
            Color.fromRGB(85, 85, 85),
            Material.GRAY_STAINED_GLASS,
            Material.GRAY_BANNER
    ),
    BLUE(ChatColor.BLUE,
            NamedTextColor.BLUE,
            Material.LIGHT_BLUE_WOOL,
            Color.fromRGB(85, 85, 255),
            Material.LIGHT_BLUE_STAINED_GLASS,
            Material.LIGHT_BLUE_BANNER
    ),
    GREEN(ChatColor.GREEN,
            NamedTextColor.GREEN,
            Material.LIME_WOOL,
            Color.fromRGB(85, 255, 85),
            Material.LIME_STAINED_GLASS,
            Material.LIME_BANNER
    ),
    AQUA(ChatColor.AQUA,
            NamedTextColor.AQUA,
            Material.CYAN_WOOL,
            Color.fromRGB(85, 255, 255),
            Material.CYAN_STAINED_GLASS,
            Material.CYAN_BANNER
    ),
    RED(ChatColor.RED,
            NamedTextColor.RED,
            Material.RED_WOOL,
            Color.fromRGB(255, 85, 85),
            Material.RED_STAINED_GLASS,
            Material.RED_BANNER
    ),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE,
            NamedTextColor.LIGHT_PURPLE,
            Material.PINK_WOOL,
            Color.fromRGB(255, 85, 255),
            Material.PINK_STAINED_GLASS,
            Material.PINK_BANNER
    ),
    YELLOW(ChatColor.YELLOW,
            NamedTextColor.YELLOW,
            Material.YELLOW_WOOL,
            Color.fromRGB(255, 255, 85),
            Material.YELLOW_STAINED_GLASS,
            Material.YELLOW_BANNER
    ),
    WHITE(ChatColor.WHITE,
            NamedTextColor.WHITE,
            Material.WHITE_WOOL,
            Color.fromRGB(255, 255, 255),
            Material.WHITE_STAINED_GLASS,
            Material.WHITE_BANNER
    ),
    TEAM_BLUE(ChatColor.BLUE,
            NamedTextColor.BLUE,
            Material.BLUE_WOOL,
            Color.fromRGB(85, 85, 255),
            Material.BLUE_STAINED_GLASS,
            Material.BLUE_BANNER
    ),

    ;

    @Deprecated
    public final ChatColor chatColor;
    public final NamedTextColor textColor;
    public final Material wool;
    public final Color rgb;
    public final Material glass;
    public final Material banner;

    Colors(ChatColor chatColor, NamedTextColor textColor, Material wool, Color rgb, Material glass, Material banner) {
        this.chatColor = chatColor;
        this.textColor = textColor;
        this.wool = wool;
        this.rgb = rgb;
        this.glass = glass;
        this.banner = banner;
    }

}
