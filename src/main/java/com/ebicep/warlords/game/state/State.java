package com.ebicep.warlords.game.state;

import com.ebicep.warlords.game.Game;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public interface State {

    /**
     * Called when the game transitions to this state
     * Teleport players to the correct locations here
     */
    void begin();
    /**
     * Run a tick in this gamestate
     * @return A new state to transition to or null
     */
    @Nullable
    State run();

    /**
     * Called when this state is ending
     */
    void end();
    
    /**
     * Called when a player is added to the game
     * @param player The player
     * @param asSpectator If they want to join as spectator
     * @see Game#acceptsPeople()
     * @see Game#acceptsSpectators() 
     */
    default void onPlayerJoinGame(OfflinePlayer player, boolean asSpectator) {
    }
    
    /**
     * Called when a player joins the server while they were part of a game.
     * Also called directly after adding a player into the game if they were
     * online at that moment
     * @param player 
     */
    default void onPlayerReJoinGame(Player player) {
    }

    default void onPlayerQuitGame(OfflinePlayer player) {
    }
    
    int getTicksElapsed();
}
