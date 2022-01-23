package com.ebicep.warlords.maps.state;

import com.ebicep.warlords.maps.Game;
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface State {

    /**
     * Called when the game transitions to this state
     * Teleport players to the correct locations here
     */
    public void begin();
    /**
     * Run a tick in this gamestate
     * @return A new state to transition to or null
     */
    @Nullable
    public State run();

    /**
     * Called when this state is ending
     */
    public void end();
    
    /**
     * Called when a player is added to the game
     * @param player The player
     * @param asSpectator If they want to join as spectator
     * @see Game#acceptsPeople()
     * @see Game#acceptsSpectators() 
     */
    public default void onPlayerJoinGame(OfflinePlayer player, boolean asSpectator) {
    }
    
    /**
     * Called when a player joins the server while they were part of a game.
     * Also called directly after adding a player into the game if they were
     * online at that moment
     * @param player 
     */
    public default void onPlayerReJoinGame(Player player) {
    }
    public default void onPlayerQuitGame(OfflinePlayer player) {
    }
}
