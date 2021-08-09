package com.ebicep.warlords.menu;

import com.ebicep.warlords.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class Menu extends AbstractMenuBase {
    public static final BiConsumer<Menu, InventoryClickEvent> ACTION_CLOSE_MENU = (m, e) -> e.getWhoClicked().closeInventory();
    private final Inventory inventory;
    private final BiConsumer<Menu, InventoryClickEvent>[] onClick = (BiConsumer<Menu, InventoryClickEvent>[]) new BiConsumer<?, ?>[9 * 6];
    private int nextItemIndex = 0;

    public static final ItemStack MENU_CLOSE = new ItemBuilder(Material.BARRIER)
            .name(ChatColor.RED + "Close")
            .get();
    public static final ItemStack MENU_BACK = new ItemBuilder(Material.ARROW)
            .name(ChatColor.GREEN + "Back")
            .get();

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
        if (++nextItemIndex >= inventory.getSize()) {
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
        //custom menu listener - does click action only if
        //- clicked inventory has the same reference as the menu inventory
        //- not air
        //- something with size
        if (event.getClickedInventory().equals(inventory) && event.getCurrentItem().getType() != Material.AIR && event.getRawSlot() < inventory.getSize()) {
            event.setCancelled(true);
            this.onClick[event.getRawSlot()].accept(this, event);
        }
    }
}