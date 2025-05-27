package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsSecondaryAbilityRunEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final AbstractAbility ability;

    public WarlordsSecondaryAbilityRunEvent(@Nonnull WarlordsEntity player, AbstractAbility ability) {
        super(player);
        this.ability = ability;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public AbstractAbility getAbility() {
        return ability;
    }

}
