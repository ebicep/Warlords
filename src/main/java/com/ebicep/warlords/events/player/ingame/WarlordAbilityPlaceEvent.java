package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class WarlordAbilityPlaceEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final AbstractAbility ability;
    private final Location location;
    private boolean cancelled;

    public WarlordAbilityPlaceEvent(@Nonnull WarlordsEntity player, AbstractAbility ability, Location location) {
        super(player);
        this.ability = ability;
        this.location = location;
    }

    public AbstractAbility getAbility() {
        return ability;
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

    public Location getLocation() {
        return location;
    }

}
