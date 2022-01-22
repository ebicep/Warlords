package com.ebicep.warlords.maps;

import com.ebicep.warlords.maps.option.techincal.GameFreezeWhenOfflineOption;
import com.ebicep.warlords.maps.state.PreLobbyState;
import com.ebicep.warlords.maps.state.State;
import com.ebicep.warlords.player.WarlordsPlayer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum GameAddon {

    PRIVATE_GAME() {
        @Override
        public void modifyGame(Game game) {
            game.getOptions().add(new GameFreezeWhenOfflineOption());
            game.setMinPlayers(1);
            game.setAcceptsPlayers(false);
        }

        @Override
        public void stateHasChanged(Game game, State oldState, State newState) {
            game.setAcceptsPlayers(false);
            if(newState instanceof PreLobbyState) {
                PreLobbyState preLobbyState = (PreLobbyState) newState;
                preLobbyState.setTimer(30 * 20);
            }
        }
    },
    IMPOSTER_MODE() {

        @Override
        public void modifyGame(Game game) {
            //options.add(new GameImposterModeOption());
        }
    },
    COOLDOWN_MODE() {
        @Override
        public void warlordsPlayerCreated(Game game, WarlordsPlayer player) {
            player.setMaxHealth((int) (player.getMaxHealth() * 1.5));
            player.setHealth((int) (player.getHealth() * 1.5));
            player.setEnergyModifier(player.getEnergyModifier() * 0.5);
            player.setCooldownModifier(player.getCooldownModifier() * 0.5);
        }
        
    },
    RECORD_MODE(),;

    public void modifyGame(@Nonnull Game game) {
    }

    /**
     * Gets the maximum amount of internalPlayers allowed in a map.Map modifiers
     * such as mega games could override the map provided map maximum.
     *
     * @param map The map to check
     * @param maxPlayers The max internalPlayers from the previous step, or the
     * map maximum internalPlayers if it is the first check
     * @return The maximum amount of internalPlayers supported by the map
     */
    public int getMaxPlayers(@Nonnull GameMap map, int maxPlayers) {
        return maxPlayers;
    }
    
    @Nullable
    public State stateWillChange(@Nonnull Game game, @Nullable State oldState, @Nonnull State newState) {
        return newState;
    }
    
    public void stateHasChanged(@Nonnull Game game, @Nullable State oldState, @Nonnull State newState) {
    }
    
    public void warlordsPlayerCreated(@Nonnull Game game, @Nonnull WarlordsPlayer player) {
    }
}
