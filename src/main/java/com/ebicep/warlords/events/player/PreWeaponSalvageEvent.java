package com.ebicep.warlords.events.player;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.concurrent.atomic.AtomicInteger;

public class PreWeaponSalvageEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final AtomicInteger salvageAmount;

    public PreWeaponSalvageEvent(AtomicInteger salvageAmount) {
        this.salvageAmount = salvageAmount;
    }

    public AtomicInteger getSalvageAmount() {
        return salvageAmount;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
