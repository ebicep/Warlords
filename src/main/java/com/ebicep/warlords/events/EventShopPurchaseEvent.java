package com.ebicep.warlords.events;

import com.ebicep.warlords.pve.SpendableBuyShop;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.UUID;

public class EventShopPurchaseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final UUID uuid;
    private final SpendableBuyShop bought;

    public EventShopPurchaseEvent(UUID uuid, SpendableBuyShop bought) {
        this.uuid = uuid;
        this.bought = bought;
    }


    public UUID getUUID() {
        return uuid;
    }

    public SpendableBuyShop getBought() {
        return bought;
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
