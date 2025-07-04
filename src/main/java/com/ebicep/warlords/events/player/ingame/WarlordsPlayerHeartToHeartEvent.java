package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsPlayerHeartToHeartEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    protected boolean cancelled = false;
    private final WarlordsEntity heartTarget;

    public WarlordsPlayerHeartToHeartEvent(@Nonnull WarlordsEntity player, WarlordsEntity heartTarget) {
        super(player);
        this.heartTarget = heartTarget;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public WarlordsEntity getHeartTarget() {
        return heartTarget;
    }

}
