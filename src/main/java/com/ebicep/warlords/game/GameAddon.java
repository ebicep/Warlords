package com.ebicep.warlords.game;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.featureflags.FeatureFlags;
import com.ebicep.warlords.game.option.PreGameItemOption;
import com.ebicep.warlords.game.option.freeze.AFKDetectionOption;
import com.ebicep.warlords.game.option.freeze.GameFreezeWhenOfflineOption;
import com.ebicep.warlords.game.option.pvp.AbilityChangeOption;
import com.ebicep.warlords.game.option.pvp.ImposterModeOption;
import com.ebicep.warlords.game.option.pvp.InterchangeModeOption;
import com.ebicep.warlords.game.state.ClosedState;
import com.ebicep.warlords.game.state.PreLobbyState;
import com.ebicep.warlords.game.state.State;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.ebicep.warlords.menu.generalmenu.WarlordsShopMenu.openTeamMenu;

public enum GameAddon {

    PRIVATE_GAME(
            "Private Game",
            null,
            "Initiates a private game where no other people can join."
    ) {
        @Override
        public void modifyGame(@Nonnull Game game) {
            switch (game.getGameMode()) {
                case CAPTURE_THE_FLAG, INTERCEPTION, TEAM_DEATHMATCH, DEBUG -> {
                    game.addOption(new PreGameItemOption(5, new ItemBuilder(Material.NOTE_BLOCK)
                            .name(Component.text("Team Selector ", NamedTextColor.GREEN).append(Component.text("(Right-Click)", NamedTextColor.GRAY)))
                            .lore(Component.text("Click to select your team!", NamedTextColor.YELLOW))
                            .get(), (g, p) -> openTeamMenu(p)));
                    game.addOption(new AFKDetectionOption());
                }
            }
            game.setMinPlayers(1);
            game.setAcceptsPlayers(false);
        }

        @Override
        public void stateHasChanged(@Nonnull Game game, State oldState, @Nonnull State newState) {
            if (newState instanceof ClosedState) {
                return;
            }
            if (newState instanceof PreLobbyState preLobbyState) {
                preLobbyState.setMaxTimer(ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "addons.privateGame.lobbyTimeSeconds", int.class, 30) * 20);
                preLobbyState.resetTimer();
                game.setAcceptsPlayers(false);
            }
        }
    },
    CUSTOM_GAME(
            "Custom Game",
            null,
            "Makes the game custom, preventing stats from counting."
    ) {
    },
    FREEZE_GAME(
            "Freeze Failsafe",
            null,
            "Pauses the game when a player is missing for longer than 10 seconds. The game will automatically resume when they join back."
    ) {
        @Override
        public void modifyGame(@Nonnull Game game) {
            game.addOption(new GameFreezeWhenOfflineOption());
        }
    },
    IMPOSTER_MODE(
            "Imposter Mode",
            null,
            "The game will assign players to intentionally boycott the game to make their team lose without being caught."
    ) {
        @Override
        public void modifyGame(@Nonnull Game game) {
            game.addOption(new ImposterModeOption());
        }

        @Override
        public boolean canCreateGame(@Nonnull GameManager.GameHolder holder) {
            if (!FeatureFlags.isAddonEnabled(this, null)) {
                return false;
            }
            // At the moment, only 1 game can be an imposter game at the same time
            return Warlords.getGameManager().getGames().stream().noneMatch(e -> e.getGame() != null && e.getGame().getAddons().contains(this));
        }
    },
    COOLDOWN_MODE(
            "Cooldown Mode",
            null,
            "Reduces energy costs and cooldowns by 75%"
    ) {
        @Override
        public void warlordsEntityCreated(@Nonnull Game game, @Nonnull WarlordsEntity player) {
            player.setEnergyModifier(player.getEnergyModifier() * 0.25f);
            player.getAbilities().forEach(ability -> ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                    "Cooldown Mode", 0.25f
            ));
        }
    },
    TRIPLE_HEALTH(
            "Triple Health",
            null,
            "Triples all players' health."
    ) {
        @Override
        public void warlordsEntityCreated(@Nonnull Game game, @Nonnull WarlordsEntity player) {
            player.getHealth().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Triple Health (Base)", 2f);
            player.heal();
        }
    },
    DISABLE_CRIT(
            "No Critical Hits",
            null,
            "Prevents all players from hitting critical hits."
    ) {
        @Override
        public void warlordsEntityCreated(@Nonnull Game game, @Nonnull WarlordsEntity player) {
            player.setCanCrit(false);
        }
    },
    DOUBLE_TIME(
            "Double Time",
            null,
            "Doubles the max duration of the game."
    ) {
    },
    MEGA_GAME(
            "Mega Game",
            null,
            "Allows any map to hold unlimited players."
    ) {
        @Override
        public int getMaxPlayers(@Nonnull GameMap map, int maxPlayers) {
            return 1000;
        }

    },
    INTERCHANGE_MODE(
            "Interchange Mode",
            null,
            "Players on the same team will swap locations with each other at random intervals."
    ) {
        @Override
        public void modifyGame(@Nonnull Game game) {
            game.addOption(new InterchangeModeOption());
            game.addOption(new GameFreezeWhenOfflineOption());
        }

    },
    ABILITY_CHANGE_RANDOM(
            "Ability Change (Random)",
            null,
            "Randomly changes all players' abilities at random intervals. Players are also forced to change every 4 minutes."
    ) {
        @Override
        public void modifyGame(@Nonnull Game game) {
            game.addOption(new AbilityChangeOption(AbilityChangeOption.Mode.RANDOM));
        }
    },
    ABILITY_CHANGE_ON_DEATH(
            "Ability Change (On Death)",
            null,
            "Randomly changes a player's abilities when they respawn. Players are also forced to change every 4 minutes."
    ) {
        @Override
        public void modifyGame(@Nonnull Game game) {
            game.addOption(new AbilityChangeOption(AbilityChangeOption.Mode.ON_DEATH));
        }
    },
    TOURNAMENT_MODE(
            "Tournament Mode",
            null,
            "Tournament Mode"
    ),
    RANDOM_SPEC_BOOST(
            "Random Spec Boost",
            null,
            "Gives random spec boosts to players if the game permits"
    ),

    ;

    public static final GameAddon[] VALUES = values();
    private final String name;
    @Nullable
    private final String permission;
    private final String description;

    GameAddon(String name, @Nullable String permission, String description) {
        this.name = name;
        this.permission = permission;
        this.description = description;
    }

    public boolean hasPermission(CommandSender sender) {
        return this.permission == null || sender.hasPermission(permission);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void modifyGame(@Nonnull Game game) {
    }

    /**
     * Gets the maximum amount of internalPlayers allowed in a map.Map modifiers
     * such as mega games could override the map provided map maximum.
     *
     * @param map        The map to check
     * @param maxPlayers The max internalPlayers from the previous step, or the
     *                   map maximum internalPlayers if it is the first check
     *
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

    public void warlordsEntityCreated(@Nonnull Game game, @Nonnull WarlordsEntity player) {
    }

    public boolean canCreateGame(@Nonnull GameManager.GameHolder holder) {
        return FeatureFlags.isAddonEnabled(this, null);
    }

}
