package com.ebicep.warlords.game.option.marker;

import com.ebicep.warlords.game.Game;

/**
 * A *marker* interface for marking interfaces as a game marker. Gamemarkers
 * should be used to allow external input of marked input, such as flag capture
 * locations or spawn locations.
 *
 * @implSpec Implementations of gamemarkers *should* design their gamemarkers in
 * a functional way, <strong>if possible</strong>, as this makes it quickly to
 * register in a low line count.
 * @see FunctionalInterface
 */
public interface GameMarker {

    /**
     * Attempts to register itself to game
     *
     * @param game The game to register to
     */
    default void register(Game game) {
        Class<?>[] interfaces = this.getClass().getInterfaces();
        for (Class<?> clazz : interfaces) {
            if (clazz != GameMarker.class && GameMarker.class.isAssignableFrom(clazz)) {
                game.registerGameMarker((Class) clazz, this); // Unchecked by design
            }
        }
        Class<?> parent = this.getClass();
        while(parent != null && GameMarker.class.isAssignableFrom(parent)) {
            if (!parent.isAnonymousClass()) {
                game.registerGameMarker((Class) parent, this); // Unchecked by design
            }
            parent = parent.getSuperclass();
        }

    }
}
