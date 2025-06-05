package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsPlayerSwapEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    protected boolean cancelled = false;
    private final WarlordsEntity swappedPlayer;
    private final Location start;
    private final Location end;

    public WarlordsPlayerSwapEvent(@Nonnull WarlordsEntity player, WarlordsEntity swappedPlayer, Location start, Location end) {
        super(player);
        this.swappedPlayer = swappedPlayer;
        this.start = start;
        this.end = end;
    }

    public Location getStart() {
        return start;
    }

    public Location getEnd() {
        return end;
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

    public WarlordsEntity getSwappedPlayer() {
        return swappedPlayer;
    }

}
