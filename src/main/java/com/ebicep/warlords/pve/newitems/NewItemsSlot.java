package com.ebicep.warlords.pve.newitems;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum NewItemsSlot {

    HELMET("Helmet", Material.DIAMOND_HELMET),
    CHESTPLATE("Chestplate", Material.DIAMOND_CHESTPLATE),
    LEGGINGS("Leggings", Material.DIAMOND_LEGGINGS),
    GLOVES("Gloves", Material.HONEYCOMB),
    BOOTS("Boots", Material.DIAMOND_BOOTS),
    RING("Ring", Material.ENDER_PEARL),
    TOME("Tome", Material.BOOK),

    ;

    private final String name;
    private final Material material;
    private final ItemStack itemStack;

    NewItemsSlot(String name, Material material) {
        this.name = name;
        this.material = material;
        this.itemStack = new ItemStack(material);
    }

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
