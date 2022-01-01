
package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.maps.Game;

/**
 * A *marker* interface for marking interfaces as a game marker. Gamemarkers
 * should be used to allow external input of marked input, such as flag capture
 * locations or spawn locations.
 * 
 * @implSpec Implementations of gamemarkers *should* design their gamemarkers in a functional way, *if possible*
 */
public interface GameMarker {
    /**
     * Attempts to register itself to game
     * @param game The game to register to
     */
    public default void register(Game game) {
        game.registerGameMarker((Class<GameMarker>)this.getClass(), this);
    }
}
