package com.ebicep.warlords.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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

}