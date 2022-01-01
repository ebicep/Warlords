package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.maps.Game;
import javax.annotation.Nonnull;
/**
 * A game exists out of multiple options, who all change the behaviour of the
 * game. 
 */
public interface Option {
    /**
     * Registers this option to a game. An Option can only be registered to one
     * game, attempting to register an option to multiple game instances may
     * yield undefined behavior.
     * @param game The game 
     */
    public void register(@Nonnull Game game);
    /**
     * Runs after the register phase, used to "bake" the internal variables,
     * getting faster speeds. Options should not attempt to register any
     * gamemarkers or scoreboard handlers here, as they might be missed by
     * other options.
     * @implNote Does nothing by default
     * @param game The game 
     */
    public default void bake(@Nonnull Game game) {
    }
    /**
     * Unregisters this option fro, a game. Note that even though an Option is
     * unregistered, registering it again may give undefined behavior
     * @param game The game 
     */
    public default void unregister(@Nonnull Game game) {
    }
    /**
     * Checks if this option is allowed to run in pause mode.
     * @implNote Returns false by default
     * @return true if allowed to run in false mode
     */
    public default boolean runInPauseMode() {
        return false;
    }
    /**
     * Execute a single tick of this option
     * @implNote Does nothing by default
     * @param game The game
     */
    public default void tick(@Nonnull Game game) {
    }
}
