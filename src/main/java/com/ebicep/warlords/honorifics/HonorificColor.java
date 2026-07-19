package com.ebicep.warlords.honorifics;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

public enum HonorificColor {

    AQUA("Aqua", TextColor.color(85, 255, 255), Material.CYAN_DYE),
    RED("Red", TextColor.color(255, 85, 85), Material.RED_DYE),
    GREEN("Green", TextColor.color(85, 255, 85), Material.LIME_DYE),
    BLUE("Blue", TextColor.color(85, 85, 255), Material.BLUE_DYE),
    LIGHT_PURPLE("Light Purple", TextColor.color(255, 85, 255), Material.MAGENTA_DYE),
    WHITE("White", TextColor.color(255, 255, 255), Material.WHITE_DYE),
    DARK_PURPLE("Dark Purple", TextColor.color(150, 0, 170), Material.PURPLE_DYE),
    GOLD("Gold", TextColor.color(255, 170, 0), Material.ORANGE_DYE),
    DARK_RED("Dark Red", TextColor.color(125, 35, 35), Material.DANDELION),
    BLACK("Black", TextColor.color(25, 25, 25), Material.INK_SAC)

    ;

    public static final HonorificColor[] VALUES = values();

    private final String displayName;
    private final TextColor textColor;
    private final Material icon;

    HonorificColor(String displayName, TextColor textColor, Material icon) {
        this.displayName = displayName;
        this.textColor = textColor;
        this.icon = icon;
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

    public HonorificCost getCost() {
        return HonorificShopCosts.getColorCost(this);
    }
}
