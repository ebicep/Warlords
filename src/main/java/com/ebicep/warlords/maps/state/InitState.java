package com.ebicep.warlords.maps.state;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.GameMap;

public class InitState implements State {

    private final Game game;
    private final GameMap newMap;

    public InitState(Game game) {
        this.game = game;
        newMap = null;
    }

    public InitState(Game game, GameMap newMap) {
        this.game = game;
        this.newMap = newMap;
    }

    @Override
    public void begin() {
        game.setCooldownMode(false);
    }

    @Override
    public State run() {
        if (newMap != null) {
            game.changeMap(newMap);
            return null;
        } else {
            return new PreLobbyState(game);
        }
    }

    @Override
    public void end() {
    }

}
