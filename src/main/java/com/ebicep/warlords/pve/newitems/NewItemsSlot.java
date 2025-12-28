package com.ebicep.warlords.pve.newitems;

import com.ebicep.warlords.util.java.NamedEnum;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum NewItemsSlot implements NamedEnum {

    HELMET("Helmet", Material.DIAMOND_HELMET),
    CHESTPLATE("Chestplate", Material.DIAMOND_CHESTPLATE),
    LEGGINGS("Leggings", Material.DIAMOND_LEGGINGS),
    GLOVES("Gloves", Material.HONEYCOMB),
    BOOTS("Boots", Material.DIAMOND_BOOTS),
    RING("Ring", Material.ENDER_PEARL),
    TOME("Tome", Material.BOOK),

    ;

    public static final NewItemsSlot[] VALUES = values();

    private final String name;
    private final Material material;
    private final ItemStack itemStack;

    NewItemsSlot(String name, Material material) {
        this.name = name;
        this.material = material;
        this.itemStack = new ItemStack(material);
    }

    public NewItemsSlot next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    @Override
    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
