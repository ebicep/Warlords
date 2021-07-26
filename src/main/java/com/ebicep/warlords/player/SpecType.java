package com.ebicep.warlords.player;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum SpecType {

    DAMAGE("damage", new ItemStack(Material.NETHER_STALK, 1), "銌", ChatColor.RED),
    TANK("tank", new ItemStack(Material.CLAY_BALL, 1), "鉰", ChatColor.YELLOW),
    HEALER("healer", new ItemStack(Material.INK_SACK, 1, (short) 6), "銀", ChatColor.GREEN),

    ;

    public final String name;
    public final ItemStack itemStack;
    public final String symbol;
    public final ChatColor chatColor;

    SpecType(String name, ItemStack itemStack, String symbol, ChatColor chatColor) {
        this.name = name;
        this.itemStack = itemStack;
        this.symbol = symbol;
        this.chatColor = chatColor;
    }

    public String getColoredSymbol() {
        return chatColor + symbol;
    }

}
