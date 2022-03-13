package com.ebicep.warlords.events;

import com.ebicep.warlords.player.WarlordsPlayer;
import javax.annotation.Nonnull;

/**
 * Base event for all warlord player based events
 */
public abstract class AbstractWarlordsPlayerEvent extends AbstractWarlordsGameEvent {

    @Nonnull
    protected final WarlordsPlayer player;

    public AbstractWarlordsPlayerEvent(@Nonnull WarlordsPlayer player) {
        super(player.getGame());
        this.player = player;
    }

    @Nonnull
    public WarlordsPlayer getPlayer() {
        return player;
    }

}
