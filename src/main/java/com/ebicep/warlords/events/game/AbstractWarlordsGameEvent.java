package com.ebicep.warlords.events.game;

import com.ebicep.warlords.game.Game;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Base event for all warlord game based events
 */
public abstract class AbstractWarlordsGameEvent extends Event {

    @Nonnull
    protected final Game game;

    public AbstractWarlordsGameEvent(Game game) {
        this.game = Objects.requireNonNull(game, "game");
    }

    @Nonnull
    public Game getGame() {
        return game;
    }

}
