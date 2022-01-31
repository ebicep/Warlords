package com.ebicep.warlords.events;

import com.ebicep.warlords.maps.Game;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.bukkit.event.Event;

/**
 * Base event for all warlord game based events
 */
public abstract class WarlordsGameEvent extends Event {

    @Nonnull
    protected final Game game;

    public WarlordsGameEvent(Game game) {
        this.game = Objects.requireNonNull(game, "game");
    }

    public Game getGame() {
        return game;
    }

}
