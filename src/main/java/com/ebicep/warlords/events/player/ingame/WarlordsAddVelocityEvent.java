package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class WarlordsAddVelocityEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final String from;
    private final Vector vector;
    private boolean cancelled;

    public WarlordsAddVelocityEvent(@Nonnull WarlordsEntity player, String from, Vector vector) {
        super(player);
        this.from = from;
        this.vector = vector;
    }

    public String getFrom() {
        return from;
    }

    public Vector getVector() {
        return vector;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}