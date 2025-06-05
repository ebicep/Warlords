package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class WarlordsThrowableProjectileImpactEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final AbstractAbility ability;
    private final Location location;
    private final WarlordsEntity directHit;
    private boolean cancelled;

    public WarlordsThrowableProjectileImpactEvent(@Nonnull WarlordsEntity player, AbstractAbility ability, Location location, WarlordsEntity directHit) {
        super(player);
        this.ability = ability;
        this.location = location;
        this.directHit = directHit;
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
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public AbstractAbility getAbility() {
        return ability;
    }

    public Location getLocation() {
        return location;
    }

}
