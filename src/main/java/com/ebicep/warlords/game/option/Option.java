package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * A game exists out of multiple options, who all change the behavior of the
 * game.
 */
public interface Option {

    /**
     * Registers this option to a game. An Option can only be registered to one
     * game, attempting to register an option to multiple game instances may
     * yield undefined behavior.
     *
     * @param game The game instance
     */
    default void register(@Nonnull Game game) {
    }

    /**
     * Called when the game is started (For a typical game, a transition to the
     * <code>PlayingState</code>). Use this method to start your long-running
     * tasks
     *
     * @param game The game instance
     */
    default void start(@Nonnull Game game) {
    }

    /**
     * Called when the game transitions to an end state. Generally, the game
     * no longer accepts players, and players can leave the game without
     * affecting their standings.
     *
     * @param game The game instance
     */
    default void onGameEnding(@Nonnull Game game) {
    }

    default void sendEventStatsMessage(@Nonnull Game game, @Nonnull Player player) {
    }

    /**
     * Called when the game transitions to a closed state. This is also when any listeners and gametasks are stopped.
     *
     * @param game The game instance
     */
    default void onGameCleanup(@Nonnull Game game) {
    }

    /**
     * Checks if the given list of options is a valid game configuration. This
     * is used for checking if the current set of options for a valid game. Note
     * that even if this method returns normally, the game may still be in an
     * valid state
     *
     * @param options The list of options to check
     * @throws IllegalArgumentException If the list of options contains a
     * mistake, to be further defined by the option itself
     */
    default void checkConflicts(List<Option> options) {
    }

    /**
     * Called when a player is created
     *
     * @param player The player to act on
     */
    @Priority()
    default void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
    }

    default void afterAllWarlordsEntitiesCreated(List<WarlordsEntity> players) {
    }

    /**
     * Called to update the warlordsPlayer's inventory
     *
     * @param warlordsPlayer The warlordsPlayer to act on
     * @param player
     */
    default void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
    }

    /**
     * Called when a player's spec is changed
     *
     * @param player The player to act on
     */
    default void onSpecChange(@Nonnull WarlordsEntity player) {
    }

    /**
     * Called when a player joins the server while they were part of a game.
     * Also called directly after adding a player into the game if they were
     * online at that moment
     *
     * @param player
     */
    default void onPlayerReJoinGame(Player player) {
    }

    /**
     * Called when player stops spectating game
     *
     * @param player The player to act on
     */
    default void onPlayerQuit(Player player) {

    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Priority {
        int value() default 3;
    }

}
