package com.ebicep.warlords.events;

import com.ebicep.warlords.player.WarlordsPlayer;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class WarlordsRespawnEvent extends AbstractWarlordsPlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    protected Location respawnLocation;
    protected boolean cancelled = false;

    public WarlordsRespawnEvent(@Nonnull WarlordsPlayer player, @Nonnull Location respawnLocation) {
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
