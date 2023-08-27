package com.ebicep.warlords.events.player;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.UUID;

public class SupplyDropCallEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final UUID uuid;
    private final long amount;
    private final boolean instant;

    public SupplyDropCallEvent(UUID uuid, long amount, boolean instant) {
        this.uuid = uuid;
        this.amount = amount;
        this.instant = instant;
    }

    public UUID getUUID() {
        return uuid;
    }

    public long getAmount() {
        return amount;
    }

    public boolean isInstant() {
        return instant;
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
