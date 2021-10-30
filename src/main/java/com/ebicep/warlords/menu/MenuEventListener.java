package com.ebicep.warlords.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public class MenuEventListener implements Listener {
    public static final String METADATA_CUSTOM_INVENTORY = "CUSTOM-INVENTORY";
    private final Plugin plugin;

    public MenuEventListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent evt) {
        Optional<MetadataValue> menu = evt.getWhoClicked().getMetadata(METADATA_CUSTOM_INVENTORY).stream()
                .filter(e -> e.value() instanceof AbstractMenuBase)
                .findAny();
        menu.ifPresent(metadataValue -> ((AbstractMenuBase) metadataValue.value()).doOnClickAction(evt));

    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent evt) {
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                if(evt.getPlayer().getOpenInventory().getTopInventory().getTitle().equals("container.crafting")) {
//                    evt.getPlayer().removeMetadata(METADATA_CUSTOM_INVENTORY, Warlords.getInstance());
//                }
//            }
//        }.runTaskLater(Warlords.getInstance(), 1);

//        List<MetadataValue> meta = new ArrayList<>(evt.getPlayer().getMetadata(METADATA_CUSTOM_INVENTORY));
//        int matchedIndex = -1;
//        for (int i = 0; i < meta.size(); i++) {
//            MetadataValue mdv = meta.get(i);
//            if (mdv.value() instanceof AbstractMenuBase && ((AbstractMenuBase) mdv.value()).getInventory() == evt.getInventory()) {
//                matchedIndex = i;
//            }
//        }
//        evt.getPlayer().removeMetadata(METADATA_CUSTOM_INVENTORY, plugin);
//        if (matchedIndex >= 0) {
//            // Restore other entries
//            for (int i = 0; i < meta.size(); i++) {
//                if (matchedIndex != i) {
//                    evt.getPlayer().setMetadata(METADATA_CUSTOM_INVENTORY, meta.get(i));
//                }
//            }
//        }
    }
}