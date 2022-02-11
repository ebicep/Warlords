
package com.ebicep.warlords.game.state;

import com.ebicep.warlords.game.Game;

public class ClosedState implements State {
    private final Game game;

    public ClosedState(Game game) {
        this.game = game;
    }
    
    @Override
    public void begin() {
        if(!game.isClosed()) {
            game.close();
        }
    }

    @Override
    public State run() {
        return null;
    }

    @Override
    public void end() {
    }

}
