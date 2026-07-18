package com.ebicep.warlords.game.option.pve.anomaly;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public enum AncientRune {

    SUN("Sun", "☀", Material.GOLD_BLOCK, NamedTextColor.GOLD),
    TIDE("Tide", "≋", Material.PRISMARINE, NamedTextColor.AQUA),
    FLAME("Flame", "▲", Material.MAGMA_BLOCK, NamedTextColor.RED),
    VOID("Void", "◆", Material.OBSIDIAN, NamedTextColor.DARK_PURPLE);

    public static final AncientRune[] VALUES = values();

    private final String name;
    private final String symbol;
    private final Material material;
    private final NamedTextColor color;

    AncientRune(String name, String symbol, Material material, NamedTextColor color) {
        this.name = name;
        this.symbol = symbol;
        this.material = material;
        this.color = color;
    }

    public Component getComponent() {
        return Component.text(symbol, color);
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public Material getMaterial() {
        return material;
    }

    public NamedTextColor getColor() {
        return color;
    }
}
