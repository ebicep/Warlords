package com.ebicep.warlords.events;

import com.ebicep.warlords.player.WarlordsPlayer;
import javax.annotation.Nonnull;

public abstract class WarlordsPlayerEvent extends WarlordsGameEvent {

    @Nonnull
    protected final WarlordsPlayer player;

    public WarlordsPlayerEvent(@Nonnull WarlordsPlayer player) {
        super(player.getGame());
        this.player = player;
    }

    @Nonnull
    public WarlordsPlayer getPlayer() {
        return player;
    }

}
