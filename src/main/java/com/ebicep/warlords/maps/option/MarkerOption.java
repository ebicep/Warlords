package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.option.marker.GameMarker;
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
    protected GameMarker marker;

    /**
     * Creates a new instance of {@code MarkerOption} which register the
     * specified {@code GameMarker}
     *
     * @param marker The gameMarker to register on the optionRegister phase
     */
    public MarkerOption(@Nonnull GameMarker marker) {
        this.marker = marker;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.marker.register(game);
    }

}
