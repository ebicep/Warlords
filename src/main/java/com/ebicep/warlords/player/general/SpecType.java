package com.ebicep.warlords.player.general;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum SpecType {

    DAMAGE("Damage", new ItemStack(Material.NETHER_WART, 1), "銌", NamedTextColor.RED),
    TANK("Tank", new ItemStack(Material.CLAY_BALL, 1), "鉰", NamedTextColor.YELLOW),
    HEALER("Healer", new ItemStack(Material.CYAN_DYE), "銀", NamedTextColor.GREEN),

    ;

    public static final SpecType[] VALUES = values();
    public final String name;
    public final ItemStack itemStack;
    public final String symbol;
    public final NamedTextColor textColor;

    SpecType(String name, ItemStack itemStack, String symbol, NamedTextColor textColor) {
        this.name = name;
        this.itemStack = itemStack;
        this.symbol = symbol;
        this.textColor = textColor;
    }

    public Component getColoredSymbol() {
        return Component.text(symbol, textColor);
    }

}
