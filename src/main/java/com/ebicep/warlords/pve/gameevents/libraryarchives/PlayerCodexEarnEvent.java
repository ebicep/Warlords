package com.ebicep.warlords.pve.gameevents.libraryarchives;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.UUID;

public class PlayerCodexEarnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final UUID uuid;
    private final PlayerCodex playerCodex;

    public PlayerCodexEarnEvent(UUID uuid, PlayerCodex playerCodex) {
        this.uuid = uuid;
        this.playerCodex = playerCodex;
    }

    public UUID getUUID() {
        return uuid;
    }

    public PlayerCodex getPlayerCodex() {
        return playerCodex;
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
