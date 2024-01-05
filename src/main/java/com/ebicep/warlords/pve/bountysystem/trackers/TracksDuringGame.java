package com.ebicep.warlords.pve.bountysystem.trackers;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;

public interface TracksDuringGame {

    /**
     * Resets the local cached tracker (the one used in apply())
     */
    void reset();

    /**
     * @param game The game that is being tracked
     * @return Whether this tracker should track given game, some trackers are gamemode specific etc.
     */
    default boolean trackGame(Game game) {
        return true;
    }

    /**
     * Called when the game ends, this is where you should apply the changes to the bounty
     * <p>
     * Not auto added to the counter in the case of the game not counting
     */
    default void apply(AbstractBounty bounty) {
        bounty.setValue(bounty.getValue() + getNewValue());
    }

    long getNewValue();

}
