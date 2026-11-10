package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsHorseAbilityDismountEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    protected boolean cancelled = false;
    private final AbstractAbility ability;

    public WarlordsHorseAbilityDismountEvent(@Nonnull WarlordsEntity player, @Nonnull AbstractAbility ability) {
        super(player);
        this.ability = ability;
    }

    @Nonnull
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

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
