package com.ebicep.warlords.menu;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class MenuEventListener implements Listener {
    public static final String METADATA_CUSTOM_INVENTORY = "CUSTOM-INVENTORY";
    private final Plugin plugin;

    public MenuEventListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent evt) {
        Optional<MetadataValue> menu = evt
                .getWhoClicked()
                .getMetadata(METADATA_CUSTOM_INVENTORY).stream()
                .filter(e -> e.value() instanceof AbstractMenuBase)
                .findAny();
        menu.ifPresent(metadataValue -> ((AbstractMenuBase) metadataValue.value()).doOnClickAction(evt));

    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent evt) {
        HumanEntity player = evt.getPlayer();
        new BukkitRunnable() {

            @Override
            public void run() {
                if (PlainTextComponentSerializer.plainText().serialize(player.getOpenInventory().title()).equals("container.crafting")) {
                    player.removeMetadata(METADATA_CUSTOM_INVENTORY, plugin);
                }
            }
        }.runTaskLater(plugin, 1);

    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        event.getPlayer().removeMetadata(METADATA_CUSTOM_INVENTORY, plugin);
    }


}