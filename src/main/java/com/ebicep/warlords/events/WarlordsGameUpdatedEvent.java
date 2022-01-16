package com.ebicep.warlords.events;

import com.ebicep.warlords.maps.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WarlordsGameUpdatedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Game game;
    private final String key;

    public WarlordsGameUpdatedEvent(Game game, String key) {
        this.game = game;
        this.key = key;
    }

    public Game getGame() {
        return game;
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