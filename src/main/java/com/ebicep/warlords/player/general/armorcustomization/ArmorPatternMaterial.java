package com.ebicep.warlords.player.general.armorcustomization;

import com.ebicep.warlords.honorifics.HonorificCost;
import com.ebicep.warlords.pve.Currencies;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;

public enum ArmorPatternMaterial {

    COPPER("Copper", TrimMaterial.COPPER, Material.COPPER_INGOT, false),
    DIAMOND("Diamond", TrimMaterial.DIAMOND, Material.DIAMOND, false),
    GOLD("Gold", TrimMaterial.GOLD, Material.GOLD_INGOT, false),
    IRON("Iron", TrimMaterial.IRON, Material.IRON_INGOT, false),
    LAPIS("Lapis", TrimMaterial.LAPIS, Material.LAPIS_LAZULI, true),
    QUARTZ("Quartz", TrimMaterial.QUARTZ, Material.QUARTZ, true),
    REDSTONE("Redstone", TrimMaterial.REDSTONE, Material.REDSTONE, true),
    EMERALD("Emerald", TrimMaterial.EMERALD, Material.EMERALD, true),
    NETHERITE("Netherite", TrimMaterial.NETHERITE, Material.NETHERITE_INGOT, true),
    AMETHYST("Amethyst", TrimMaterial.AMETHYST, Material.AMETHYST_SHARD, true),
    RESIN("Resin Brick", TrimMaterial.RESIN, Material.RESIN_BRICK, true),

    ;

    public static final ArmorPatternMaterial[] VALUES = values();
    public static final HonorificCost COST = HonorificCost.of(Currencies.FAIRY_ESSENCE, 50_000);

    private final String displayName;
    private final TrimMaterial trimMaterial;
    private final Material icon;
    private final boolean supporterExclusive;

    ArmorPatternMaterial(String displayName, TrimMaterial trimMaterial, Material icon, boolean supporterExclusive) {
        this.displayName = displayName;
        this.trimMaterial = trimMaterial;
        this.icon = icon;
        this.supporterExclusive = supporterExclusive;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TrimMaterial getTrimMaterial() {
        return trimMaterial;
    }

    public Material getIcon() {
        return icon;
    }

    public boolean isSupporterExclusive() {
        return supporterExclusive;
    }

    public HonorificCost getCost() {
        return supporterExclusive ? null : COST;
    }

}
