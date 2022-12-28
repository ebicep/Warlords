package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class WarlordsAddVelocityEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Vector vector;

    public WarlordsAddVelocityEvent(@Nonnull WarlordsEntity player, Vector vector) {
        super(player);
        this.vector = vector;
    }

    public Vector getVector() {
        return vector;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}