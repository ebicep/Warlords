package com.ebicep.warlords.maps.state;

import javax.annotation.Nullable;

public interface State {

    /**
     * Called when the game transitions to this state
     * Teleport players to the correct locations here
     */
    public void begin();

    /**
     * Run a tick in this gamestate
     *
     * @return A new state to transition to or null
     */
    @Nullable
    public State run();

    /**
     * Called when this state is ending
     */
    public void end();
}
