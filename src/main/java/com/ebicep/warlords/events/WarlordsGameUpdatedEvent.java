package com.ebicep.warlords.events;

import com.ebicep.warlords.maps.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WarlordsGameUpdatedEvent extends WarlordsGameEvent {

    private static final HandlerList handlers = new HandlerList();
    private final String key;

    public WarlordsGameUpdatedEvent(Game game, String key) {
        super(game);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}