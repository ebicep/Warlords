package com.ebicep.warlords.events;

import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WarlordsDeathEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final WarlordsPlayer player;

    public WarlordsDeathEvent(WarlordsPlayer player) {
        this.player = player;
    }

    public WarlordsPlayer getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}