package com.ebicep.warlords.maps.state;

import com.ebicep.warlords.maps.Game;

public class InitState implements State {

    private final Game game;

    public InitState(Game game) {
        this.game = game;
    }

    @Override
    public void begin( ) {
        game.setCooldownMode(false);
    }

    @Override
    public State run() {
        return new PreLobbyState(game);
    }

    @Override
    public void end( ) {
    }

}
