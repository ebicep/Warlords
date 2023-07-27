package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.events.EventFlags;
import com.ebicep.warlords.events.game.AbstractWarlordsGameEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * Base event for all warlord player based events
 */
public abstract class AbstractWarlordsEntityEvent extends AbstractWarlordsGameEvent {

    @Nonnull
    protected final WarlordsEntity player;
    protected EnumSet<EventFlags> flags = EnumSet.allOf(EventFlags.class);

    public AbstractWarlordsEntityEvent(@Nonnull WarlordsEntity player) {
        super(player.getGame());
        this.player = player;
    }

    @Nonnull
    public WarlordsEntity getWarlordsEntity() {
        return player;
    }

    @Override
    public EnumSet<EventFlags> getFlags() {
        return flags;
    }
}
