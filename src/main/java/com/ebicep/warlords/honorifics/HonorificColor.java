package com.ebicep.warlords.honorifics;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

public enum HonorificColor {

    AQUA("Aqua", TextColor.color(85, 255, 255), Material.BLUE_BANNER, false),
    YELLOW("Yellow", TextColor.color(255, 255, 50), Material.YELLOW_BANNER, false),
    RED("Red", TextColor.color(255, 85, 85), Material.RED_BANNER, false),
    GREEN("Green", TextColor.color(85, 255, 85), Material.GREEN_BANNER, false),
    BLUE("Blue", TextColor.color(85, 85, 255), Material.LIGHT_BLUE_BANNER, false),
    LIGHT_PURPLE("Light Purple", TextColor.color(255, 85, 255), Material.PURPLE_BANNER, false),
    WHITE("White", TextColor.color(255, 255, 255), Material.WHITE_BANNER, false),
    DARK_PURPLE("Dark Purple", TextColor.color(150, 0, 170), Material.CRYING_OBSIDIAN, true),
    GOLD("Gold", TextColor.color(255, 170, 0), Material.GOLD_INGOT, true),
    DARK_RED("Dark Red", TextColor.color(125, 35, 35), Material.NETHER_WART_BLOCK, true),
    BLACK("Black", TextColor.color(25, 25, 25), Material.OBSIDIAN, true)

    ;

    public static final HonorificColor[] VALUES = values();

    private final String displayName;
    private final TextColor textColor;
    private final Material icon;
    private final boolean patreonExclusive;

    HonorificColor(String displayName, TextColor textColor, Material icon, boolean patreonExclusive) {
        this.displayName = displayName;
        this.textColor = textColor;
        this.icon = icon;
        this.patreonExclusive = patreonExclusive;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TextColor getTextColor() {
        return textColor;
    }

    public Material getIcon() {
        return icon;
    }

    public boolean isPatreonExclusive() {
        return patreonExclusive;
    }

    public HonorificCost getCost() {
        return HonorificShopCosts.getColorCost(this);
    }
}
