package com.ebicep.warlords.events;

import com.ebicep.warlords.player.ingame.AbstractWarlordsEntity;

import javax.annotation.Nonnull;

/**
 * Base event for all warlord player based events
 */
public abstract class AbstractWarlordsPlayerEvent extends AbstractWarlordsGameEvent {

    @Nonnull
    protected final AbstractWarlordsEntity player;

    public AbstractWarlordsPlayerEvent(@Nonnull AbstractWarlordsEntity player) {
        super(player.getGame());
        this.player = player;
    }

    @Nonnull
    public AbstractWarlordsEntity getPlayer() {
        return player;
    }

}
