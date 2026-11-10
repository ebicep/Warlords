package com.ebicep.warlords.pve.newitems.events;

import com.ebicep.warlords.pve.newitems.NewItem;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.UUID;

public class NewItemCraftEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final UUID uuid;
    private final NewItem item;

    public NewItemCraftEvent(UUID uuid, NewItem item) {
        this.uuid = uuid;
        this.item = item;
    }

    public UUID getUUID() {
        return uuid;
    }

    public NewItem getItem() {
        return item;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
