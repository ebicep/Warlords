package com.ebicep.warlords.honorifics;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.mobs.MobDrop;
import org.bukkit.Material;

import javax.annotation.Nullable;

public enum Honorific {

    THE_MIGHTY_ROLLER("The Mighty Roller", "Reroll items 90 times.", Material.REPEATER, null),
    GOLDEN("Golden", "Purchased from The Artificer.", Material.GOLD_INGOT, HonorificCost.of(Currencies.COIN, 100_000_000)),
    SYNTHESIZER("Synthesizer", "Synthesize 100 Star Pieces.", Material.NETHER_STAR, null),
    EXTREMA("Extrema", "Complete Extreme Wave Defense in under 8 minutes.", Material.NETHERITE_SWORD, null),
    ONE_FOR_ALL("One for All", "Defeat the One of Nine boss.", Material.WITHER_SKELETON_SKULL, null),
    LEGEND("Legend", "Purchased from The Artificer.", Material.BLAZE_POWDER, HonorificCost.of(Currencies.LEGEND_FRAGMENTS, 500_000)),
    THE_HEART_THIEF("The Heart Thief", "Defeat the Lilium boss.", Material.HEART_OF_THE_SEA, null),
    BOUNDLESS("Boundless", "Complete 200 consecutive Endless waves.", Material.END_CRYSTAL, null),
    GOLEM("Golem", "Defeat 50,000 Iron Golems.", Material.IRON_BLOCK, null),
    GOD_OF_WAR("God of War", "Defeat 2,000,000 mobs.", Material.NETHERITE_SWORD, null),
    EXPLORER("Explorer", "Reach floor 30 in Ancient Renegades.", Material.COMPASS, null),
    TREASURER("Treasurer", "Reach floor 100 in Ancient Renegades.", Material.CHEST, null),
    SLAUGHTERER("Slaughterer", "Survive 180 minutes in Onslaught.", Material.REDSTONE, null),
    SYNTHETIC("Synthetic", "Purchased from The Artificer.", Material.AMETHYST_SHARD, HonorificCost.of(Currencies.SYNTHETIC_SHARD, 1_000_000)),
    STAR_GUIDE("Star Guide", "Use 1,000 Star Pieces.", Material.NETHER_STAR, null),
    HARMONIOUS("Harmonious", "Purchased from The Artificer.", Material.MAGENTA_DYE, HonorificCost.of(Currencies.FAIRY_ESSENCE, 500_000)),
    SUPPLIER("Supplier", "Roll 50,000 Supply Drop Tokens.", Material.FIREWORK_STAR, null),
    CHARM_OF_ZENITH("Charm of Zenith", "Purchased from The Artificer.", Material.WIND_CHARGE, HonorificCost.of(MobDrop.ZENITH_STAR, 1_000)),
    LIMITLESS("Limitless", "Purchased from The Artificer.", Material.CLOCK, HonorificCost.of(Currencies.LIMIT_BREAKER, 500)),
    PRESTIGIOUS("Prestigious", "Reach prestige 30 on any class.", Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, null),
    SKELETRON("Skeletron", "Defeat 50,000 Skeletons.", Material.SKELETON_SKULL, null),
    CHAMPION("Champion", "Defeat 10,000 Champion-tier mobs.", Material.TOTEM_OF_UNDYING, null),
    COLATORAL("Colatoral", "Deal 1 billion damage in a single game.", Material.TNT, null),
    TWO_FATES("Two Fates", "Complete the Regnum of Two Crowns raid.", Material.GOLDEN_SWORD, null),
    CROWNED_HEIR("Crowned Heir", "Complete Regnum of Two Crowns on Oblivion with only 4 players.", Material.GOLDEN_HELMET, null),
    ASCENDED("Ascended", "Purchased from The Artificer.", Material.ECHO_SHARD, HonorificCost.of(Currencies.ASCENDANT_SHARD, 5_000));

    public static final Honorific[] VALUES = values();

    private final String displayName;
    private final String requirement;
    private final Material icon;
    @Nullable
    private final HonorificCost cost;

    Honorific(String displayName, String requirement, Material icon, @Nullable HonorificCost cost) {
        this.displayName = displayName;
        this.requirement = requirement;
        this.icon = icon;
        this.cost = cost;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRequirement() {
        return requirement;
    }

    public Material getIcon() {
        return icon;
    }

    @Nullable
    public HonorificCost getCost() {
        return cost;
    }

    public boolean isPurchasable() {
        return cost != null;
    }
}
