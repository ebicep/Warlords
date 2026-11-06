package com.ebicep.warlords.player.general.armorcustomization;

import org.bukkit.Material;

public enum ArmorPiece {

    HELMET("Helmet", 3, false, Material.DIAMOND_HELMET),
    CHESTPLATE("Chestplate", 2, true, Material.DIAMOND_CHESTPLATE),
    LEGGINGS("Leggings", 1, true, Material.DIAMOND_LEGGINGS),
    BOOTS("Boots", 0, true, Material.DIAMOND_BOOTS),

    ;

    public static final ArmorPiece[] VALUES = values();

    private final String displayName;
    private final int armorIndex;
    private final boolean supportsTrim;
    private final Material preview;

    ArmorPiece(String displayName, int armorIndex, boolean supportsTrim, Material preview) {
        this.displayName = displayName;
        this.armorIndex = armorIndex;
        this.supportsTrim = supportsTrim;
        this.preview = preview;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getArmorIndex() {
        return armorIndex;
    }

    public boolean supportsTrim() {
        return supportsTrim;
    }

    public Material getPreview() {
        return preview;
    }

}
