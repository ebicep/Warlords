package com.ebicep.warlords.events.game;

import com.ebicep.warlords.events.EventFlags;
import com.ebicep.warlords.game.Game;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Objects;

/**
 * Base event for all warlord game based events
 */
public abstract class AbstractWarlordsGameEvent extends Event {

    @Nonnull
    protected final Game game;
    protected EnumSet<EventFlags> flags = EnumSet.allOf(EventFlags.class);

    public AbstractWarlordsGameEvent(Game game) {
        this.game = Objects.requireNonNull(game, "game");
    }

    @Nonnull
    public Game getGame() {
        return game;
    }

    public EnumSet<EventFlags> getEventFlags() {
        return flags;
    }
}
