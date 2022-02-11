package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.marker.GameMarker;

import javax.annotation.Nonnull;

/**
 * Simple utility option to quickly register a gamemarker
 *
 * @see GameMarker
 */
public class MarkerOption implements Option {

    /**
     * The marker that will be registered when register is called
     */
    @Nonnull
    protected GameMarker[] markers;

    /**
     * Creates a new instance of {@code MarkerOption} which register the
     * specified {@code GameMarker}
     *
     * @param markers The game marker's to register on the register phase
     */
    public MarkerOption(@Nonnull GameMarker... markers) {
        this.markers = markers;
    }

    @Override
    public void register(@Nonnull Game game) {
        for (GameMarker marker : markers) {
            marker.register(game);
        }
    }

}
