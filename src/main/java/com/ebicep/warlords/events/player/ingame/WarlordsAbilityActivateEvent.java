package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsAbilityActivateEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final AbstractAbility ability;
    private boolean cancelled;

    public WarlordsAbilityActivateEvent(@Nonnull WarlordsEntity player, AbstractAbility ability) {
        super(player);
        this.ability = ability;
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

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
