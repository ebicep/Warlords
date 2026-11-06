package com.ebicep.warlords.player.general.mounts;

import com.ebicep.warlords.honorifics.HonorificCost;
import com.ebicep.warlords.pve.Currencies;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum MountArmor {

    LEATHER("Leather", Material.LEATHER_HORSE_ARMOR, false),
    IRON("Iron", Material.IRON_HORSE_ARMOR, false),
    COPPER("Copper", Material.COPPER_HORSE_ARMOR, true),
    GOLD("Gold", Material.GOLDEN_HORSE_ARMOR, true),
    DIAMOND("Diamond", Material.DIAMOND_HORSE_ARMOR, true),
    NETHERITE("Netherite", Material.NETHERITE_HORSE_ARMOR, true),

    ;

    public static final MountArmor[] VALUES = values();
    public static final HonorificCost COST = HonorificCost.of(Currencies.FAIRY_ESSENCE, 50_000);

    private final String displayName;
    private final Material icon;
    private final boolean supporterExclusive;

    MountArmor(String displayName, Material icon, boolean supporterExclusive) {
        this.displayName = displayName;
        this.icon = icon;
        this.supporterExclusive = supporterExclusive;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }

    public boolean isSupporterExclusive() {
        return supporterExclusive;
    }

    public HonorificCost getCost() {
        return COST;
    }

    public ItemStack createItem() {
        ItemStack item = new ItemStack(icon);
        item.editMeta(meta -> meta.setUnbreakable(true));
        return item;
    }

}
