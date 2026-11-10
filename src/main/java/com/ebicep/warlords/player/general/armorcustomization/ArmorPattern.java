package com.ebicep.warlords.player.general.armorcustomization;

import com.ebicep.warlords.honorifics.HonorificCost;
import com.ebicep.warlords.pve.Currencies;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimPattern;

public enum ArmorPattern {

    SENTRY("Sentry", TrimPattern.SENTRY, Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, false),
    VEX("Vex", TrimPattern.VEX, Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, false),
    WILD("Wild", TrimPattern.WILD, Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, false),
    COAST("Coast", TrimPattern.COAST, Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, false),
    DUNE("Dune", TrimPattern.DUNE, Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, false),
    WAYFINDER("Wayfinder", TrimPattern.WAYFINDER, Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, false),
    RAISER("Raiser", TrimPattern.RAISER, Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, true),
    SHAPER("Shaper", TrimPattern.SHAPER, Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, true),
    HOST("Host", TrimPattern.HOST, Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, true),
    WARD("Ward", TrimPattern.WARD, Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, true),
    SNOUT("Snout", TrimPattern.SNOUT, Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, true),
    RIB("Rib", TrimPattern.RIB, Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, true),
    SPIRE("Spire", TrimPattern.SPIRE, Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, true),
    TIDE("Tide", TrimPattern.TIDE, Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, true),
    EYE("Eye", TrimPattern.EYE, Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, true),
    BOLT("Bolt", TrimPattern.BOLT, Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, true),
    SILENCE("Silence", TrimPattern.SILENCE, Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, true),
    FLOW("Flow", TrimPattern.FLOW, Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, true),

    ;

    public static final ArmorPattern[] VALUES = values();
    public static final HonorificCost COST = HonorificCost.of(Currencies.FAIRY_ESSENCE, 100_000);

    private final String displayName;
    private final TrimPattern trimPattern;
    private final Material icon;
    private final boolean supporterExclusive;

    ArmorPattern(String displayName, TrimPattern trimPattern, Material icon, boolean supporterExclusive) {
        this.displayName = displayName;
        this.trimPattern = trimPattern;
        this.icon = icon;
        this.supporterExclusive = supporterExclusive;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TrimPattern getTrimPattern() {
        return trimPattern;
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
