package com.ebicep.warlords.pve.rewards;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Currencies {

    SYNTHETIC_SHARD(
            "Synthetic Shard",
            ChatColor.WHITE,
            new ItemStack(Material.BLAZE_POWDER)
    ),
    LEGEND_FRAGMENTS(
            "Legend Fragments",
            ChatColor.GOLD,
            new ItemStack(Material.GOLD_NUGGET)
    ),
    FAIRY_ESSENCE(
            "Fairy Essence",
            ChatColor.LIGHT_PURPLE,
            new ItemStack(Material.INK_SACK, 1, (short) 13)
    ),
    COMMON_STAR_PIECE(
            "Common Star Piece",
            ChatColor.GREEN,
            new ItemStack(Material.NETHER_STAR)
    ),
    RARE_STAR_PIECE(
            "Rare Star Piece",
            ChatColor.BLUE,
            new ItemStack(Material.NETHER_STAR)
    ),
    EPIC_STAR_PIECE(
            "Epic Star Piece",
            ChatColor.DARK_PURPLE,
            new ItemStack(Material.NETHER_STAR)
    ),
    LEGENDARY_STAR_PIECE(
            "Legendary Star Piece",
            ChatColor.GOLD,
            new ItemStack(Material.NETHER_STAR)
    ),
    SUPPLY_DROP_TOKEN(
            "Supply Drop Token",
            ChatColor.YELLOW,
            new ItemStack(Material.FIREWORK_CHARGE)
    ),
    COIN(
            "Coin",
            ChatColor.YELLOW,
            new ItemStack(Material.GOLD_NUGGET)
    ),
    SKILL_BOOST_MODIFIER(
            "Skill Boost Modifier",
            ChatColor.BLACK,
            new ItemStack(Material.GOLD_NUGGET)
    ),


    ;

    public final String name;
    public final ChatColor chatColor;
    public final ItemStack item;

    Currencies(String name, ChatColor chatColor, ItemStack item) {
        this.name = name;
        this.chatColor = chatColor;
        this.item = item;
    }

    public String getColoredName() {
        return chatColor + name;
    }

}
