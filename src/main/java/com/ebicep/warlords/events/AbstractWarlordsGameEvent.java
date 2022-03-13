package com.ebicep.warlords.events;

import com.ebicep.warlords.game.Game;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.bukkit.event.Event;

/**
 * Base event for all warlord game based events
 */
public abstract class AbstractWarlordsGameEvent extends Event {

    @Nonnull
    protected final Game game;

    public AbstractWarlordsGameEvent(Game game) {
        this.game = Objects.requireNonNull(game, "game");
    }

    public Game getGame() {
        return game;
    }

}
