package com.ebicep.warlords.menu;

import com.ebicep.warlords.Warlords;
import static com.ebicep.warlords.menu.MenuEventListener.METADATA_CUSTOM_INVENTORY;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Menu extends MenuBase {
    public static final BiConsumer<Menu, InventoryClickEvent> ACTION_CLOSE_MENU = (m, e) -> e.getWhoClicked().closeInventory();
    private final Inventory inventory;
    private final BiConsumer<Menu, InventoryClickEvent>[] onClick = (BiConsumer<Menu, InventoryClickEvent>[]) new BiConsumer<?, ?>[9*6];
    private int nextItemIndex = 0;

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Menu(String name, int size) {
        this.inventory = Bukkit.createInventory(null, size, name);
    }
    public void setItem(int x, int y, ItemStack item, BiConsumer<Menu, InventoryClickEvent> clickHandler) {
        setItem(x + y * 9, item, clickHandler);
    }

    public void setItem(int index, ItemStack item, BiConsumer<Menu, InventoryClickEvent> clickHandler) {
        inventory.setItem(index, item);
        onClick[index] = clickHandler;
        if(++nextItemIndex >= inventory.getSize()) {
            nextItemIndex = 0;
        }
    }
    public void removeItem(int x, int y) {
        removeItem(x + y * 9);
    }

    public void removeItem(int index) {
        inventory.setItem(index, null);
        onClick[index] = null;
    }

    public void addItem(ItemStack item, BiConsumer<Menu, InventoryClickEvent> clickHandler) {
        this.setItem(nextItemIndex, item, clickHandler);
    }


    @Override
    public void doOnClickAction(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getRawSlot() < inventory.getSize()) {
            this.onClick[event.getRawSlot()].accept(this, event);
        }
    }
}