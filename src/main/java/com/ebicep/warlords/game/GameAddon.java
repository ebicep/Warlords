package com.ebicep.warlords.game;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.option.GameFreezeWhenOfflineOption;
import com.ebicep.warlords.game.option.ImposterModeOption;
import com.ebicep.warlords.game.option.InterchangeModeOption;
import com.ebicep.warlords.game.option.PreGameItemOption;
import com.ebicep.warlords.game.state.ClosedState;
import com.ebicep.warlords.game.state.PreLobbyState;
import com.ebicep.warlords.game.state.State;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ItemBuilder;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum GameAddon {

    PRIVATE_GAME("Private Game", null) {
        @Override
        public void modifyGame(@Nonnull Game game) {
            game.getOptions().add(new GameFreezeWhenOfflineOption());
            game.getOptions().add(new PreGameItemOption(5, new ItemBuilder(Material.NOTE_BLOCK)
                    .name(ChatColor.GREEN + "Team Selector " + ChatColor.GRAY + "(Right-Click)")
                    .lore(ChatColor.YELLOW + "Click to select your team!")
                    .get()));
            game.setMinPlayers(1);
            game.setAcceptsPlayers(false);
        }

        @Override
        public void stateHasChanged(@Nonnull Game game, State oldState, @Nonnull State newState) {
            if (newState instanceof ClosedState) {
                return;
            }
            if (newState instanceof PreLobbyState) {
                PreLobbyState preLobbyState = (PreLobbyState) newState;
                preLobbyState.setMaxTimer(30 * 20);
                preLobbyState.resetTimer();
                game.setAcceptsPlayers(false);
            }
        }
    },
    IMPOSTER_MODE("Imposter Mode", "warlords.game.impostertoggle") {
        @Override
        public void modifyGame(@Nonnull Game game) {
            game.getOptions().add(new ImposterModeOption());
        }

        @Override
        public boolean canCreateGame(@Nonnull GameManager.GameHolder holder) {
            // At the moment, only 1 game can be an imposter game at the same time
            return !Warlords.getGameManager().getGames().stream().anyMatch(e -> e.getGame() != null && e.getGame().getAddons().contains(this));
        }
    },
    COOLDOWN_MODE("Cooldown Mode", "warlords.game.cooldowngame") {
        @Override
        public void warlordsPlayerCreated(@Nonnull Game game, @Nonnull WarlordsPlayer player) {
            player.setMaxHealth((int) (player.getMaxHealth() * 1.5));
            player.setHealth((int) (player.getHealth() * 1.5));
            player.setEnergyModifier(player.getEnergyModifier() * 0.5);
            player.setCooldownModifier(player.getCooldownModifier() * 0.5);
        }
    },
    //RECORD_MODE(),
    MEGA_GAME("Mega Game", "warlords.game.megagame") {
        @Override
        public int getMaxPlayers(@Nonnull GameMap map, int maxPlayers) {
            return Integer.MAX_VALUE;
        }

    },
    INTERCHANGE_MODE("Interchange Mode", null) {
        @Override
        public void modifyGame(@Nonnull Game game) {
            game.getOptions().add(new InterchangeModeOption());
            game.getOptions().add(new GameFreezeWhenOfflineOption());
        }

    };

    private final String name;
    @Nullable
    private final String permission;

    GameAddon(String name, @Nullable String permission) {
        this.name = name;
        this.permission = permission;
    }

    public boolean hasPermission(CommandSender sender) {
        return this.permission == null || sender.hasPermission(permission);
    }

    public String getName() {
        return name;
    }

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

    public boolean canCreateGame(@Nonnull GameManager.GameHolder holder) {
        return true;
    }
}
