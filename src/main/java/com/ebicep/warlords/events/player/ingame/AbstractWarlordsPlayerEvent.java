package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.events.game.AbstractWarlordsGameEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;

import javax.annotation.Nonnull;

/**
 * Base event for all warlord player based events
 */
public abstract class AbstractWarlordsPlayerEvent extends AbstractWarlordsGameEvent {

    @Nonnull
    protected final WarlordsEntity player;

    public AbstractWarlordsPlayerEvent(@Nonnull WarlordsEntity player) {
        super(player.getGame());
        this.player = player;
    }

    @Nonnull
    public WarlordsEntity getPlayer() {
        return player;
    }

}
