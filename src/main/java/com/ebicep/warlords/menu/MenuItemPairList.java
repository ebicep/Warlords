package com.ebicep.warlords.menu;

import com.ebicep.warlords.util.java.Pair;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class MenuItemPairList {

    private final List<Pair<ItemStack, BiConsumer<Menu, InventoryClickEvent>>> itemPairs = new ArrayList<>();

    public MenuItemPairList() {
    }

    public void add(ItemStack item, BiConsumer<Menu, InventoryClickEvent> action) {
        itemPairs.add(new Pair<>(item, action));
    }

    public int size() {
        return itemPairs.size();
    }

    public Pair<ItemStack, BiConsumer<Menu, InventoryClickEvent>> get(int index) {
        return itemPairs.get(index);
    }

    public List<Pair<ItemStack, BiConsumer<Menu, InventoryClickEvent>>> getItemPairs() {
        return itemPairs;
    }
}
