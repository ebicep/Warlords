package com.ebicep.warlords.pve.items.events;

import com.ebicep.warlords.pve.items.types.AbstractItem;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ItemScrapEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final UUID uuid;
    private final AbstractItem item;

    public ItemScrapEvent(UUID uuid, AbstractItem item) {
        this.uuid = uuid;
        this.item = item;
    }

    public UUID getUUID() {
        return uuid;
    }

    public AbstractItem getItem() {
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
