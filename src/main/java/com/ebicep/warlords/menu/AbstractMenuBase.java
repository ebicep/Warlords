package com.ebicep.warlords.menu;

import com.ebicep.warlords.Warlords;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

public abstract class AbstractMenuBase {
    protected abstract Inventory getInventory();

    public abstract void doOnClickAction(InventoryClickEvent event);

    public void openForPlayer(Player player) {
        //player.closeInventory();
        player.removeMetadata(MenuEventListener.METADATA_CUSTOM_INVENTORY, Warlords.getInstance());
        player.setMetadata(MenuEventListener.METADATA_CUSTOM_INVENTORY, new FixedMetadataValue(Warlords.getInstance(), this));
        player.openInventory(getInventory());
    }


}