package com.ebicep.warlords.maps;

import com.ebicep.warlords.maps.option.techincal.GameFreezeWhenOfflineOption;

public enum GameAddon {

    PRIVATE_GAME() {
        @Override
        public void modifyGame(Game game) {
            super.modifyGame(game);
            game.getOptions().add(new GameFreezeWhenOfflineOption());
            game.setMinPlayers(1);
        }
    },
    IMPOSTER_MODE() {

        @Override
        public void modifyGame(Game game) {
            super.modifyGame(game);
            //options.add(new GameImposterModeOption());
        }
    },
    COOLDOWN_MODE() {
    },
    RECORD_MODE(),;

    public void modifyGame(Game game) {
    }

    /**
     * Gets the maximum amount of players allowed in a map.Map modifiers such
 as mega games could override the map provided map maximum.
     *
     * @param map The map to check
     * @param maxPlayers The max players from the previous step, or the map
     * maximum players if it is the first check
     * @return The maximum amount of players supported by the map
     */
    public int getMaxPlayers(GameMap map, int maxPlayers) {
        return maxPlayers;
    }
}
