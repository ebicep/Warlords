package com.ebicep.warlords.events.player;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.Objects;

public class WarlordsRespawnEvent extends AbstractWarlordsPlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    protected Location respawnLocation;
    protected boolean cancelled = false;

    public WarlordsRespawnEvent(@Nonnull WarlordsEntity player, @Nonnull Location respawnLocation) {
        super(player);
        this.respawnLocation = Objects.requireNonNull(respawnLocation, "respawnLocation");
    }

    @Nonnull
    public Location getRespawnLocation() {
        return respawnLocation;
    }

    public void setRespawnLocation(@Nonnull Location respawnLocation) {
        this.respawnLocation = Objects.requireNonNull(respawnLocation, "respawnLocation");
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
