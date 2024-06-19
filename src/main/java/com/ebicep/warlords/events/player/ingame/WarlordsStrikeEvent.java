package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsStrikeEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final AbstractAbility strikeAbility;
    private final WarlordsEntity strikedEntity;

    public WarlordsStrikeEvent(@Nonnull WarlordsEntity player, AbstractAbility strikeAbility, WarlordsEntity strikedEntity) {
        super(player);
        this.strikeAbility = strikeAbility;
        this.strikedEntity = strikedEntity;
    }

    public AbstractAbility getStrikeAbility() {
        return strikeAbility;
    }

    public WarlordsEntity getStrikedEntity() {
        return strikedEntity;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
