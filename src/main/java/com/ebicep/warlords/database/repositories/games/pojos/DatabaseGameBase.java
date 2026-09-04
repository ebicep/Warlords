package com.ebicep.warlords.database.repositories.games.pojos;

import co.aikar.commands.CommandIssuer;
import co.aikar.taskchain.TaskChain;
import com.ebicep.holograms.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractAbilityStats;
import com.ebicep.warlords.achievements.Achievement;
import com.ebicep.warlords.achievements.types.TieredAchievements;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import com.ebicep.warlords.database.leaderboards.guilds.GuildLeaderboardManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.*;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.DateUtil;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.TriFunction;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.chat.ChatChannels.sendDebugMessage;

public abstract class DatabaseGameBase<T extends DatabaseGamePlayerBase> {

    public static final Location LAST_GAME_STATS_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, 26.5, 83, 184.5, 150, 0);
    public static final Location TOP_DAMAGE_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, 37.5, 82, 181.5, 90, 0);
    public static final Location TOP_HEALING_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, 32.5, 82, 188.5, 135, 0);
    public static final Location TOP_ABSORBED_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, 25.5, 82, 193.5, 180, 0);
    public static final Location TOP_DHP_PER_MINUTE_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, -3.5, 82, 184.5, -150, 0);
    public static final Location TOP_DAMAGE_ON_CARRIER_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, -14.5, 83, 181.5, -90, 0);
    public static final Location TOP_HEALING_ON_CARRIER_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, -9.5, 83, 188.5, -135, 0);
    public static final Location PLAYER_ABILITY_STATS_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, -2.5, 83, 193.5, 180, 0);
    public static final Location GAME_SWITCH_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, 16.5, 83, 184.55, 180, 0);
    public static final Location PLAYER_ABILITY_STATS_SWITCH_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, 6.5, 83, 184.55, 180, 0);
    public static final List<DatabaseGameBase> previousGames = new ArrayList<>();
    public static final int MAX_GAMES = 5;
    protected static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

    public static boolean addGame(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean updatePlayerStats) {
        try {
            if (!GameMode.isPvE(game.getGameMode())) {
                Optional<WarlordsPlayer> highestDamagePlayer = game.warlordsPlayers()
                        .max(Comparator.comparing((WarlordsPlayer wp) -> wp.getMinuteStats().total().getDamage()));
                Optional<WarlordsPlayer> highestHealingPlayer = game.warlordsPlayers()
                        .max(Comparator.comparing((WarlordsPlayer wp) -> wp.getMinuteStats().total().getHealing()));
                if (highestDamagePlayer.isPresent() && highestHealingPlayer.isPresent()) {
                    float highestDamage = highestDamagePlayer.get().getMinuteStats().total().getDamage();
                    float highestHealing = highestHealingPlayer.get().getMinuteStats().total().getHealing();
                    //checking for inflated stats
                    if (highestDamage > 750000 || highestHealing > 750000) {
                        updatePlayerStats = false;
                        ChatUtils.MessageType.WARLORDS.sendMessage("NOT UPDATING PLAYER STATS - Game exceeds 750k damage / healing");
                    }
                }
            } else {
                for (Option option : game.getOptions()) {
                    if (option instanceof WaveDefenseOption waveDefenseOption) {
                        if (waveDefenseOption.getDifficulty() != DifficultyIndex.EVENT && waveDefenseOption.getWavesCleared() == 0) {
                            ChatUtils.MessageType.WARLORDS.sendMessage("NOT UPDATING PLAYER STATS - Wave Defense game cleared 0 waves");
                            updatePlayerStats = false;
                            break;
                        }
                    }
                }
            }
            //check for private + untracked gamemodes
            if (game.getAddons().contains(GameAddon.PRIVATE_GAME)) {
                switch (game.getGameMode()) {
                    case DUEL, DEBUG -> updatePlayerStats = false;
                }
            }

            //Any game with these game addons will not record player stats
            for (GameAddon addon : game.getAddons()) {
                if (!updatePlayerStats) {
                    break;
                }
                switch (addon) {
                    case CUSTOM_GAME, IMPOSTER_MODE, COOLDOWN_MODE, TRIPLE_HEALTH, INTERCHANGE_MODE,
                         ABILITY_CHANGE_RANDOM, ABILITY_CHANGE_ON_DEATH -> {
                        ChatUtils.MessageType.WARLORDS.sendMessage("NOT UPDATING PLAYER STATS - Some addon detected");
                        updatePlayerStats = false;
                    }
                }
            }

            if (updatePlayerStats) {
                ChatUtils.MessageType.WARLORDS.sendMessage("UPDATING PLAYER STATS " + game.getGameId());

                if (!game.getAddons().contains(GameAddon.CUSTOM_GAME)) {
                    //CHALLENGE ACHIEVEMENTS
                    game.warlordsPlayers()
                        .forEachOrdered(warlordsPlayer -> {
                            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(warlordsPlayer.getUuid());
                            databasePlayer.addAchievements(warlordsPlayer.getAchievementsUnlocked());
                        });
                    if (!GameMode.isPvE(game.getGameMode())) {
                        Warlords.newChain()
                                .async(() -> ChatUtils.MessageType.WARLORDS.sendMessage(DatabaseGameCTF.getWarlordsPlusEndGameStats(game)))
                                .execute();
                    }
                }
            }

            TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> createDatabaseGame = game.getGameMode().getCreateDatabaseGame();
            if (createDatabaseGame == null) {
                ChatUtils.MessageType.GAME_SERVICE.sendMessage("Cannot add game to database - the collection has not been configured");
                return false;
            }
            DatabaseGameBase databaseGame = createDatabaseGame.apply(game, gameWinEvent, updatePlayerStats);
            if (databaseGame == null) {
                ChatUtils.MessageType.GAME_SERVICE.sendMessage("Cannot add game to database - null database game");
                return false;
            }

            scheduleGamesBacklogJson(game, databaseGame);

            if (previousGames.size() >= MAX_GAMES) {
                previousGames.get(0).deleteHolograms();
                previousGames.remove(0);
            }
            previousGames.add(databaseGame);
            StatsLeaderboardManager.PLAYER_LEADERBOARD_INFOS.values().forEach(PlayerLeaderboardInfo::resetGameHologram);
            databaseGame.createHolograms();

            if (!game.getAddons().contains(GameAddon.CUSTOM_GAME)) {
                addGameToDatabase(databaseGame, null);
            } else if (game.playersCount() >= 16 && game.getAddons().contains(GameAddon.PRIVATE_GAME)) {
                Warlords.newChain()
                        .async(() -> DatabaseManager.gameService.createBackup(databaseGame))
                        .execute();
                addGameToDatabase(databaseGame, null);
            }

            if (updatePlayerStats && GameMode.isPvE(game.getGameMode())) {
                GuildLeaderboardManager.recalculateAllLeaderboards();
            }

            Bukkit.getOnlinePlayers().forEach(DatabaseGameBase::setGameHologramVisibility);

            //sending message if player information remained the same
            ChatChannels.sendDebugMessage((CommandIssuer) null,
                    Component.text((updatePlayerStats ?
                                    "This game was added to the database and player information was updated" :
                                    "This game was added to the database but player information remained the same"), NamedTextColor.GREEN
                    )
            );
        } catch (Exception e) {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Error adding game to database");
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage(e);

            TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> createDatabaseGame = game.getGameMode().getCreateDatabaseGame();
            if (createDatabaseGame == null) {
                ChatUtils.MessageType.GAME_SERVICE.sendMessage("Cannot add game to database - the collection has not been configured");
                return false;
            }
            DatabaseGameBase databaseGame = createDatabaseGame.apply(game, gameWinEvent, updatePlayerStats);
            if (databaseGame == null) {
                ChatUtils.MessageType.GAME_SERVICE.sendMessage("Cannot add game to database - null database game after error");
                return false;
            }
            scheduleGamesBacklogJson(game, databaseGame);
            Warlords.newChain()
                    .async(() -> DatabaseManager.gameService.createBackup(databaseGame))
                    .execute();
            addGameToDatabase(databaseGame, null);
        }
        return updatePlayerStats;
    }

    private static void scheduleGamesBacklogJson(@Nonnull Game game, DatabaseGameBase databaseGame) {
        if (!(databaseGame instanceof DatabaseGameCTF databaseGameCTF) || !game.getAddons().contains(GameAddon.PRIVATE_GAME)) {
            return;
        }
        ChatUtils.MessageType.GAME_SERVICE.sendMessage(
                "Scheduling games-backlog JSON send - id=" + databaseGameCTF.getId()
                        + ", addons=" + game.getAddons()
                        + ", players=" + game.playersCount()
        );
        Warlords.newChain()
                .async(() -> DatabaseGameCTF.sendGamesBacklogJson(databaseGameCTF))
                .execute();
    }

    public void deleteHolograms() {
        holograms.forEach(Hologram::deleteHologram);
        holograms.clear();
        playerStatsHolograms.forEach(Hologram::deleteHologram);
        playerStatsHolograms.clear();
    }

    public void createHolograms() {
        List<Hologram> holograms = new ArrayList<>();

        //readding game holograms
        ComponentBuilder lastGameStatsComponent = ComponentBuilder.create("Last " + (isPrivate() ? "Private" : "Pub") + " Game Stats", NamedTextColor.AQUA, TextDecoration.BOLD);
        ComponentBuilder topDamageComponent = ComponentBuilder.create("Top Damage", NamedTextColor.AQUA, TextDecoration.BOLD);
        ComponentBuilder topHealingComponent = ComponentBuilder.create("Top Healing", NamedTextColor.AQUA, TextDecoration.BOLD);
        ComponentBuilder topAbsorbedComponent = ComponentBuilder.create("Top Absorbed", NamedTextColor.AQUA, TextDecoration.BOLD);

//        TextHologramData topDHPPerMinuteData = new TextHologramData("game_stats_dhp" + exactDate, DatabaseGameBase.TOP_DHP_PER_MINUTE_LOCATION);
//        topDHPPerMinuteData.setPersistent(false);
//        topDHPPerMinuteData.removeLine(0);
//        topDHPPerMinuteData.addLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top DHP per Minute");

        //last game stats
        appendLastGameStats(lastGameStatsComponent);

        Set<? extends DatabaseGamePlayerBase> allPlayers = getBasePlayers();
        HashMap<DatabaseGamePlayerBase, ChatColor> playerColor = new HashMap<>();
        for (DatabaseGamePlayerBase allPlayer : allPlayers) {
            Team team = getTeam(allPlayer);
            if (team != null) {
                playerColor.put(allPlayer, team.getChatColor());
            }
        }

        List<String> players = new ArrayList<>();

        for (String s : Utils.SPECS_ORDERED) {
//            ComponentBuilder componentBuilder = ComponentBuilder.create(s, NamedTextColor.AQUA).text(": "); TODO
            StringBuilder playerSpecs = new StringBuilder("  " + ChatColor.AQUA + s).append(": ");
            final boolean[] add = {false};
            allPlayers.stream()
                      .filter(o -> o.getSpec().name.equalsIgnoreCase(s))
                      .sorted((o1, o2) -> Integer.compare(getTeam(o1).ordinal(), getTeam(o2).ordinal()))
                      .forEach(p -> {
                          playerSpecs.append(playerColor.getOrDefault(p, ChatColor.WHITE))
                                     .append(p.getName())
                                     .append(p.getKDAString())
                                     .append(ChatColor.GRAY)
                                     .append(", ");
                          add[0] = true;
                      });
            if (add[0]) {
                playerSpecs.setLength(playerSpecs.length() - 2);
                playerSpecs.append("  ");
                players.add(playerSpecs.toString());
            }
        }
        players.forEach(s -> lastGameStatsComponent.newLine(s));

        //top dmg/healing/absorbed + dhp per game + dmg/heal on carrier
        List<String> topDamagePlayers = new ArrayList<>();
        List<String> topHealingPlayers = new ArrayList<>();
        List<String> topAbsorbedPlayers = new ArrayList<>();

        Map<ChatColor, Long> totalDamage = new HashMap<>();
        Map<ChatColor, Long> totalHealing = new HashMap<>();
        Map<ChatColor, Long> totalAbsorbed = new HashMap<>();

        allPlayers.stream()
                  .sorted(Comparator.comparingLong(DatabaseGamePlayerBase::getTotalDamage).reversed())
                  .forEach(databaseGamePlayer -> {
                      totalDamage.merge(playerColor.get(databaseGamePlayer), databaseGamePlayer.getTotalDamage(), Long::sum);
                      topDamagePlayers.add("  " + playerColor.get(databaseGamePlayer) + databaseGamePlayer.getName() + ": " +
                              ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalDamage()) + "  ");
                  });

        allPlayers.stream()
                  .sorted(Comparator.comparingLong(DatabaseGamePlayerBase::getTotalHealing).reversed())
                  .forEach(databaseGamePlayer -> {
                      totalHealing.merge(playerColor.get(databaseGamePlayer), databaseGamePlayer.getTotalHealing(), Long::sum);
                      topHealingPlayers.add("  " + playerColor.get(databaseGamePlayer) + databaseGamePlayer.getName() + ": " +
                              ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalHealing()) + "  ");
                  });

        allPlayers.stream()
                  .sorted(Comparator.comparingLong(DatabaseGamePlayerBase::getTotalAbsorbed).reversed())
                  .forEach(databaseGamePlayer -> {
                      totalAbsorbed.merge(playerColor.get(databaseGamePlayer), databaseGamePlayer.getTotalAbsorbed(), Long::sum);
                      topAbsorbedPlayers.add("  " + playerColor.get(databaseGamePlayer) + databaseGamePlayer.getName() + ": " +
                              ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalAbsorbed()) + "  ");
                  });

        appendTeamDHP(topDamageComponent, totalDamage);
        appendTeamDHP(topHealingComponent, totalHealing);
        appendTeamDHP(topAbsorbedComponent, totalAbsorbed);

        topDamagePlayers.forEach(topDamageComponent::newLine);
        topHealingPlayers.forEach(topHealingComponent::newLine);
        topAbsorbedPlayers.forEach(topAbsorbedComponent::newLine);

        addCustomHolograms(holograms);

        HologramDataText lastGameStatsData = new HologramDataText.Builder<>(lastGameStatsComponent.build())
                .setBillboard(Display.Billboard.FIXED)
                .build();
        Hologram lastGameStats = new Hologram.Builder("lastGameStats" + exactDate,
                LAST_GAME_STATS_LOCATION,
                p -> lastGameStatsData
        ).build();
        HologramDataText topDamageData = new HologramDataText.Builder<>(topDamageComponent.build())
                .setBillboard(Display.Billboard.FIXED)
                .build();
        Hologram topDamage = new Hologram.Builder("topDamage" + exactDate,
                TOP_DAMAGE_LOCATION,
                p -> topDamageData
        ).build();
        HologramDataText topHealingData = new HologramDataText.Builder<>(topHealingComponent.build())
                .setBillboard(Display.Billboard.FIXED)
                .build();
        Hologram topHealing = new Hologram.Builder("topHealing" + exactDate,
                TOP_HEALING_LOCATION,
                p -> topHealingData
        ).build();
        HologramDataText topAbsorbedData = new HologramDataText.Builder<>(topAbsorbedComponent.build())
                .setBillboard(Display.Billboard.FIXED)
                .build();
        Hologram topAbsorbed = new Hologram.Builder("topAbsorbed" + exactDate,
                TOP_ABSORBED_LOCATION,
                p -> topAbsorbedData
        ).build();

        holograms.add(lastGameStats);
        holograms.add(topDamage);
        holograms.add(topHealing);
        holograms.add(topAbsorbed);
//        Hologram topDHPPerMinute = hologramManager.create(topDHPPerMinuteData);
//        holograms.add(topDHPPerMinute);

        HologramDataText playerAbilityStatsSwitcherData = new HologramDataText.Builder<>(ComponentBuilder
                .create("Player Ability Stats", NamedTextColor.AQUA, TextDecoration.UNDERLINED)
                .build()
        )
                .setBillboard(Display.Billboard.VERTICAL)
                .build();
        Hologram playerAbilityStatsSwitcher = new Hologram.Builder("playerAbilityStatsSwitcher" +
                exactDate,
                DatabaseGameBase.PLAYER_ABILITY_STATS_SWITCH_LOCATION,
                p -> playerAbilityStatsSwitcherData
        ).build();

        holograms.add(playerAbilityStatsSwitcher);

        List<HologramDataText> playerData = new ArrayList<>();
        List<T> sortedPlayerBySpec = getBasePlayers()
                .stream()
                .sorted((o1, o2) -> {
                    int teamCompare = Integer.compare(getTeam(o1).ordinal(), getTeam(o2).ordinal());
                    if (teamCompare == 0) {
                        return Integer.compare(o1.getSpec().ordinal(), o2.getSpec().ordinal());
                    }
                    return teamCompare;
                })
                .toList();
        for (int i = 0; i < sortedPlayerBySpec.size(); i++) {
            T player = sortedPlayerBySpec.get(i);
            int finalI = i;
            playerData.add(new HologramDataText.Builder<>(ComponentBuilder
                            .create(player.getSpec().name + ": ", NamedTextColor.AQUA)
                            .text(player.getName(), getTeam(player).getTeamColor())
                            .build()
                    )
                            .setBillboard(Display.Billboard.VERTICAL)
                            .setComponentModifier(componentModifier -> {
                                Player p = componentModifier.player();
                                Component component = componentModifier.component();

                                PlayerLeaderboardInfo playerLeaderboardInfo = StatsLeaderboardManager.getPlayerInfo(p);
                                int currentPlayerIndex = playerLeaderboardInfo.getGameHologramPlayerAbilityStats(this);
                                return currentPlayerIndex == finalI ? component.color(NamedTextColor.GREEN) : component;
                            })
                            .build()
            );
        }

        // sort by team first then spec
        List<HologramDataText> playerAbilityStats = new ArrayList<>();
        getBasePlayers()
                .stream()
                .sorted((o1, o2) -> {
                    int teamCompare = Integer.compare(getTeam(o1).ordinal(), getTeam(o2).ordinal());
                    if (teamCompare == 0) {
                        return Integer.compare(o1.getSpec().ordinal(), o2.getSpec().ordinal());
                    }
                    return teamCompare;
                })
                .forEachOrdered(player -> {
                    ComponentBuilder componentBuilder = ComponentBuilder.create("" + getTeam(player).getChatColor() + ChatColor.BOLD + player.getName() + "'s Ability Stats")
                                                                        .newLine();
                    Map<Ability<?>, AbstractAbilityStats<?, ?>> abilityStats = player.getAbilityStats();
                    for (Map.Entry<Ability<?>, AbstractAbilityStats<?, ?>> entry : abilityStats.entrySet()) {
                        Ability<?> ability = entry.getKey();
                        if (ability == null || ability.create == null) {
                            componentBuilder.newLine("ERROR");
                            continue;
                        }
                        AbstractAbilityStats<?, ?> abstractAbilityStats = entry.getValue();
                        AbstractAbility abstractAbility = ability.create.get();
                        abstractAbility.init(abstractAbility.getBuilder());
                        TextColor abilityColor = abstractAbility.getAbilityColor();
                        ChatColor color = ChatColor.GRAY;
                        if (abilityColor.equals(NamedTextColor.GREEN)) {
                            color = ChatColor.GREEN;
                        } else if (abilityColor.equals(NamedTextColor.RED)) {
                            color = ChatColor.RED;
                        } else if (abilityColor.equals(NamedTextColor.LIGHT_PURPLE)) {
                            color = ChatColor.LIGHT_PURPLE;
                        } else if (abilityColor.equals(NamedTextColor.AQUA)) {
                            color = ChatColor.AQUA;
                        } else if (abilityColor.equals(NamedTextColor.GOLD)) {
                            color = ChatColor.GOLD;
                        }
                        componentBuilder.newLine(color + abstractAbility.getName());
                        abstractAbilityStats.getStatsDisplay().forEach(abilityStatDisplay -> {
                            componentBuilder.newLine(ChatColor.WHITE + abilityStatDisplay.name() + ": " + ChatColor.GOLD + abilityStatDisplay.value());
                        });
                        componentBuilder.newLine();
                    }
                    playerAbilityStats.add(new HologramDataText.Builder<>(componentBuilder.build())
                            .setBillboard(Display.Billboard.FIXED)
                            .build()
                    );
                });

        Hologram playerStatsHologram = new Hologram.Builder("playerAbilityStatsPlayer" + exactDate,
                PLAYER_ABILITY_STATS_LOCATION,
                p -> {
                    StatsLeaderboardManager.validatePlayerHolograms(p);
                    PlayerLeaderboardInfo playerLeaderboardInfo = StatsLeaderboardManager.PLAYER_LEADERBOARD_INFOS.get(p.getUniqueId());
                    return playerAbilityStats.get(playerLeaderboardInfo.getGameHologramPlayerAbilityStats(this));
                }
        ).build();
        holograms.add(playerStatsHologram);

        Location location = DatabaseGameBase.PLAYER_ABILITY_STATS_SWITCH_LOCATION.clone().add(0, -1.25, 0);
        List<Hologram> playerAbilityStatsSwitcherHolograms = new ArrayList<>();
        InteractData interactData = new InteractData(2f, -1, true);
        for (int i = 0; i < 3 && i < playerData.size(); i++) {
            int finalI = i;
            Hologram.Builder builder = new Hologram.Builder("playerAbilityStatsPlayerSwitcher" + finalI +
                    exactDate,
                    location.clone(),
                    p -> {
                        PlayerLeaderboardInfo playerLeaderboardInfo = StatsLeaderboardManager.getPlayerInfo(p);
                        int currentPlayerIndex = playerLeaderboardInfo.getGameHologramPlayerAbilityStats(this);
                        HologramDataText playerDataText;
                        if (finalI == 0) {
                            if (currentPlayerIndex == 0) {
                                playerDataText = playerData.getLast();
                            } else {
                                playerDataText = playerData.get(currentPlayerIndex - 1);
                            }
                        } else if (finalI == 1) {
                            playerDataText = playerData.get(currentPlayerIndex);
                        } else {
                            if (currentPlayerIndex == playerData.size() - 1) {
                                playerDataText = playerData.getFirst();
                            } else {
                                playerDataText = playerData.get(currentPlayerIndex + 1);
                            }
                        }
                        return playerDataText;
                    }
            );
            if (finalI == 0 || finalI == 2 || finalI == 1 && playerData.size() < 3) {
                builder.setInteract(player -> {
                            if (playerData.size() == 1) {
                                return false;
                            }
                            StatsLeaderboardManager.validatePlayerHolograms(player);
                            PlayerLeaderboardInfo playerLeaderboardInfo = StatsLeaderboardManager.PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId());
                            int currentPlayerIndex = playerLeaderboardInfo.getGameHologramPlayerAbilityStats(this);
                            if (finalI == 0) {
                                playerLeaderboardInfo.setGameHologramPlayerAbilityStats(currentPlayerIndex - 1 < 0 ? playerData.size() - 1 : currentPlayerIndex - 1);
                                playerAbilityStatsSwitcherHolograms.forEach(hologram -> HologramManager.updateHologram(player, hologram));
                                HologramManager.updateHologram(player, playerStatsHologram);
                                return false;
                            }
                            if (finalI == 1 && playerData.size() < 3 || finalI == 2) {
                                playerLeaderboardInfo.setGameHologramPlayerAbilityStats(currentPlayerIndex + 1 >= playerData.size() ? 0 : currentPlayerIndex + 1);
                                playerAbilityStatsSwitcherHolograms.forEach(hologram -> HologramManager.updateHologram(player, hologram));
                                HologramManager.updateHologram(player, playerStatsHologram);
                                return false;
                            }
                            return false;
                        }, player -> interactData
                );
            }
            Hologram playerAbilityStatsPlayerSwitcher = builder.build();
            holograms.add(playerAbilityStatsPlayerSwitcher);
            playerAbilityStatsSwitcherHolograms.add(playerAbilityStatsPlayerSwitcher);
            location.add(0, 0.4, 0);
        }

        holograms.forEach(hologram -> {
            HologramManager.addHologram(hologram);
        });

        this.holograms = holograms;
    }

    public static void addGameToDatabase(DatabaseGameBase databaseGame, Player player) {
        if (DatabaseManager.gameService == null) {
            return;
        }
        GamesCollections collection = databaseGame.getGameMode().getGamesCollections();
        databaseGame.gameAddons.remove(GameAddon.CUSTOM_GAME);
        Warlords.newChain()
                .asyncFirst(() -> {
                    try {
                        return DatabaseManager.gameService.exists(databaseGame, collection);
                    } catch (Exception e) {
                        Warlords.newChain()
                                .async(() -> DatabaseManager.gameService.createBackup(databaseGame))
                                .execute();
                        ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage(e);
                        return null;
                    }
                })
                .syncLast(exists -> {
                    if (exists == null) {
                        return;
                    }
                    try {
                        //game in the database
                        if (exists) {
                            if (player != null) {
                                sendDebugMessage(player, Component.text("Game Found", NamedTextColor.GREEN));
                            }
                            //if not counted then update player stats then set counted to true, else do nothing
                            if (!databaseGame.isCounted()) {
                                if (player != null) {
                                    sendDebugMessage(player, Component.text("Updating Player Stats", NamedTextColor.GREEN));
                                }
                                databaseGame.updatePlayerStatsFromGame(databaseGame, 1);
                                databaseGame.setCounted(true);
                                DatabaseManager.updateGameAsync(databaseGame);
                            }
                        } else {
                            if (player != null) {
                                sendDebugMessage(player, Component.text("Game Not Found", NamedTextColor.GREEN));
                            }
                            //game not in database then add game and update player stats if counted
                            if (databaseGame.isCounted()) {
                                if (player != null) {
                                    sendDebugMessage(player, Component.text("Updating Player Stats", NamedTextColor.GREEN));
                                }
                                databaseGame.updatePlayerStatsFromGame(databaseGame, 1);
                            }
                            if (player != null) {
                                sendDebugMessage(player, Component.text("Creating Game", NamedTextColor.GREEN));
                            }
                            //only add game if comps
                            //if (databaseGame.isPrivate) {
                            TaskChain<?> taskChain = Warlords.newChain()
                                                             .delay(4, TimeUnit.SECONDS)
                                                             .async(() -> DatabaseManager.gameService.create(databaseGame, collection));
                            for (PlayersCollections activeCollection : PlayersCollections.ACTIVE_LEADERBOARD_COLLECTIONS) {
                                taskChain.delay(10, TimeUnit.SECONDS)
                                         .sync(() -> StatsLeaderboardManager.resetLeaderboards(activeCollection, databaseGame.getGameMode()));
                            }
                            taskChain.sync(StatsLeaderboardManager::setLeaderboardHologramVisibilityToAll)
                                     .execute();
                            //}
                        }
                    } catch (Exception e) {
                        Warlords.newChain()
                                .async(() -> DatabaseManager.gameService.createBackup(databaseGame))
                                .execute();
                        ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage(e);
                    }
                })
                .execute();
    }

    public static void setGameHologramVisibility(Player player) {
        int selectedGame = StatsLeaderboardManager.getPlayerInfo(player).getGameHologram();
        for (int i = 0; i < previousGames.size(); i++) {
            DatabaseGameBase<?> databaseGameBase = previousGames.get(i);
            List<Hologram> gameHolograms = databaseGameBase.getHolograms();
            List<Hologram> statsHolograms = databaseGameBase.getPlayerStatsHolograms();
            if (i == selectedGame) {
                gameHolograms.forEach(hologram -> hologram.getVisibilityManager().addViewer(player));
                databaseGameBase.refreshHolograms(player);
            } else {
                gameHolograms.forEach(hologram -> hologram.getVisibilityManager().removeViewer(player));
                statsHolograms.forEach(hologram -> hologram.getVisibilityManager().removeViewer(player));
            }
        }
    }

    public boolean isPrivate() {
        return gameAddons.contains(GameAddon.PRIVATE_GAME);
    }

    public abstract void appendLastGameStats(ComponentBuilder componentBuilder);

    public abstract Set<T> getBasePlayers();

    public abstract Team getTeam(DatabaseGamePlayerBase player);

    protected void appendTeamDHP(ComponentBuilder componentBuilder, Map<ChatColor, Long> map) {
        map.entrySet().stream().sorted(Map.Entry.<ChatColor, Long>comparingByValue().reversed()).forEach(chatColorLongEntry -> {
            ChatColor key = chatColorLongEntry.getKey();
            Long value = chatColorLongEntry.getValue();
            componentBuilder.newLine(key + (key == ChatColor.BLUE ? "Blue: " : "Red: ") + ChatColor.YELLOW + NumberFormat.addCommaAndRound(value));
        });
        componentBuilder.newLine();
    }

    public abstract void addCustomHolograms(List<Hologram> holograms);

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public boolean isCounted() {
        return counted;
    }

    public abstract void updatePlayerStatsFromGame(DatabaseGameBase<T> databaseGame, int multiplier);

    public List<Hologram> getHolograms() {
        if (holograms.isEmpty()) {
            createHolograms();
        }
        return holograms;
    }

    public List<Hologram> getPlayerStatsHolograms() {
        return playerStatsHolograms;
    }

    public void refreshHolograms(Player player) {

    }

    public void setCounted(boolean counted) {
        this.counted = counted;
    }

    public static void createGameSwitcherHologram() {
        if (!Warlords.hologramsEnabled) {
            return;
        }
        HologramDataText gameSwitcherData = new HologramDataText.Builder<>(ComponentBuilder
                .create("Last " + previousGames.size() + " Games", NamedTextColor.AQUA, TextDecoration.UNDERLINED)
                .build()
        )
                .setBillboard(Display.Billboard.VERTICAL)
                .build();
        Hologram gameSwitcher = new Hologram.Builder("gameSwitcher",
                DatabaseGameBase.GAME_SWITCH_LOCATION,
                p -> gameSwitcherData
        )
                .setVisibility(VisibilityType.ALL)
                .build();

        HologramManager.addHologram(gameSwitcher);

        Location location = DatabaseGameBase.GAME_SWITCH_LOCATION.clone().add(0, -1.25, 0);
        List<Hologram> gameSwitcherHolograms = new ArrayList<>();
        InteractData interactData = new InteractData(2f, -1, true);
        for (int i = 0; i < 3 && i < previousGames.size(); i++) {
            int finalI = i;
            Hologram.Builder builder = new Hologram.Builder("gameSwitcherGame" + finalI,
                    location.clone(),
                    p -> {
                        PlayerLeaderboardInfo playerLeaderboardInfo = StatsLeaderboardManager.getPlayerInfo(p);
                        int gameHologram = playerLeaderboardInfo.getGameHologram();
                        DatabaseGameBase databaseGameBase;
                        TextColor color;
                        int gameNumber;
                        if (finalI == 0) {
                            color = NamedTextColor.GRAY;
                            gameNumber = getGameBefore(gameHologram);
                            if (gameHologram == 0) {
                                databaseGameBase = previousGames.getLast();
                            } else {
                                databaseGameBase = previousGames.get(gameHologram - 1);
                            }
                        } else if (finalI == 1) {
                            color = NamedTextColor.GREEN;
                            gameNumber = gameHologram;
                            databaseGameBase = previousGames.get(gameHologram);
                        } else {
                            color = NamedTextColor.GRAY;
                            gameNumber = getGameAfter(gameHologram);
                            if (gameHologram == previousGames.size() - 1) {
                                databaseGameBase = previousGames.getFirst();
                            } else {
                                databaseGameBase = previousGames.get(gameHologram + 1);
                            }
                        }
                        boolean isLatestGame = databaseGameBase == previousGames.getLast();
                        return new HologramDataText.Builder<>(ComponentBuilder
                                .create(isLatestGame ? "Latest Game" : (gameNumber + 1) + ". " + previousGames.get(gameNumber).getDate(), color)
                                .build()
                        )
                                .setBillboard(Display.Billboard.VERTICAL)
                                .build();
                    }
            ).setVisibility(VisibilityType.ALL);
            if (finalI == 0 || finalI == 2 || finalI == 1 && previousGames.size() < 3) {
                builder.setInteract(player -> {
                            if (previousGames.size() == 1) {
                                return false;
                            }
                            PlayerLeaderboardInfo playerLeaderboardInfo = StatsLeaderboardManager.getPlayerInfo(player);
                            int gameHologram = playerLeaderboardInfo.getGameHologram();
                            if (finalI == 0) {
                                playerLeaderboardInfo.setGameHologram(getGameBefore(gameHologram));
                                gameSwitcherHolograms.forEach(hologram -> HologramManager.updateHologram(player, hologram));
                                setGameHologramVisibility(player);
                                return false;
                            }
                            if (finalI == 1 && previousGames.size() < 3 || finalI == 2) {
                                playerLeaderboardInfo.setGameHologram(getGameAfter(gameHologram));
                                gameSwitcherHolograms.forEach(hologram -> HologramManager.updateHologram(player, hologram));
                                setGameHologramVisibility(player);
                                return false;
                            }
                            return false;
                        }, player -> interactData
                );
            }

            Hologram gameSwitcherGame = builder.build();
            gameSwitcherHolograms.add(gameSwitcherGame);
            location.add(0, 0.4, 0);
        }
        gameSwitcherHolograms.forEach(HologramManager::addHologram);
    }

    private static int getGameBefore(int currentGame) {
        if (currentGame <= 0) {
            return previousGames.size() - 1;
        }
        return currentGame - 1;
    }

    private static int getGameAfter(int currentGame) {
        if (currentGame >= previousGames.size() - 1) {
            return 0;
        }
        return currentGame + 1;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static void removeGameFromDatabase(DatabaseGameBase databaseGame, Player player) {
        if (DatabaseManager.gameService == null) {
            return;
        }
        GamesCollections collection = databaseGame.getGameMode().getGamesCollections();
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.gameService.exists(databaseGame, collection))
                .syncLast(exists -> {
                    //game in the database
                    if (exists) {
                        //if counted then remove player stats then set counted to false, else do nothing
                        if (databaseGame.isCounted()) {
                            if (player != null) {
                                sendDebugMessage(player, Component.text("Updating Player Stats", NamedTextColor.GREEN));
                            }
                            databaseGame.updatePlayerStatsFromGame(databaseGame, -1);
                            databaseGame.setCounted(false);
                            DatabaseManager.updateGameAsync(databaseGame);
                        }
                    } else { //else game not in database then do nothing
                        if (player != null) {
                            sendDebugMessage(player, Component.text("Game Not Found", NamedTextColor.GREEN));
                        }
                    }
                })
                .execute();
    }

    public static void updatePlayerStatsFromTeam(DatabaseGameBase databaseGame, DatabaseGamePlayerBase gamePlayer, int multiplier) {
        //ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Updating " + gamePlayer.getName() + " stats from team");
        for (PlayersCollections activeCollection : PlayersCollections.ACTIVE_COLLECTIONS) {
            if (!activeCollection.shouldUpdate(databaseGame.getExactDate())) {
                return; //Can return because if game is not in the same week then it will not be in the same day
            }
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(gamePlayer.getUuid(), activeCollection);
            //ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Updating " + gamePlayer.getName() + " stats from team - " + activeCollection.name);
            if (GameMode.isPvE(databaseGame.getGameMode())) {
                databasePlayer.updateStats(databasePlayer, databaseGame,
                        databaseGame.getGameMode(),
                        gamePlayer,
                        DatabaseGamePlayerResult.NONE,
                        multiplier,
                        activeCollection
                );
            } else {
                // TODO check this
                databasePlayer.updateStats(databasePlayer,
                        databaseGame,
                        databaseGame.getGameMode(),
                        gamePlayer,
                        databaseGame.getPlayerGameResult(gamePlayer),
                        multiplier,
                        activeCollection
                );
            }
            if (activeCollection == PlayersCollections.LIFETIME) {
                List<Achievement.AbstractAchievementRecord<?>> achievementRecords = Arrays
                        .stream(TieredAchievements.values())
                        .filter(tieredAchievements -> tieredAchievements.gameMode == null || tieredAchievements.gameMode == databaseGame.getGameMode())
                        .filter(tieredAchievements -> tieredAchievements.databasePlayerPredicate.test(databasePlayer))
                        .filter(tieredAchievements -> databasePlayer.getAchievements()
                                                                    .stream()
                                                                    .noneMatch(abstractAchievementRecord -> abstractAchievementRecord.getAchievement() == tieredAchievements))
                        .map(TieredAchievements.TieredAchievementRecord::new)
                        .collect(Collectors.toList());
                Player player = Bukkit.getOfflinePlayer(gamePlayer.getUuid()).getPlayer();
                if (player != null) {
                    achievementRecords.forEach(record -> record.getAchievement().sendAchievementUnlockMessage(player));
                }
                databasePlayer.addAchievements(achievementRecords);
            }
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer, activeCollection);
        }
    }

    public Instant getExactDate() {
        return exactDate;
    }

    public void setExactDate(Instant exactDate) {
        this.exactDate = exactDate;
    }

    public abstract DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player);

    public static List<DatabaseGameBase> getPreviousGames() {
        return previousGames;
    }

    public static Date convertToDateFrom(String objectId) {
        return new Date(convertToTimestampFrom(objectId));
    }

    public static long convertToTimestampFrom(String objectId) {
        return Long.parseLong(objectId.substring(0, 8), 16) * 1000;
    }

    @Id
    protected String id;
    @Field("exact_date")
    protected Instant exactDate = Instant.now();
    protected String date;
    protected GameMap map;
    @Field("game_mode")
    protected GameMode gameMode = GameMode.CAPTURE_THE_FLAG;
    @Field("game_addons")
    protected List<GameAddon> gameAddons = new ArrayList<>();
    protected boolean counted = false;
    @Transient
    protected List<Hologram> holograms = new ArrayList<>();
    @Transient
    protected List<Hologram> playerStatsHolograms = new ArrayList<>();

    public DatabaseGameBase() {
    }

    public DatabaseGameBase(@Nonnull Game game, boolean counted) {
        this.exactDate = Instant.now();
        this.date = DateUtil.formatCurrentDateEST(DATE_FORMAT);
        this.map = game.getMap();
        this.gameMode = game.getGameMode();
        this.gameAddons = new ArrayList<>(game.getAddons());
        this.counted = counted;
    }

    public abstract String getGameLabel();

    public List<Component> getLore() {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Map: ", NamedTextColor.GRAY).append(Component.text(getMap().getMapName(), NamedTextColor.YELLOW)));
        lore.add(Component.text("Mode: ", NamedTextColor.GRAY).append(Component.text(getGameMode().getName(), NamedTextColor.AQUA)));
        lore.add(Component.text("Addons: ", NamedTextColor.GRAY).append(Component.text(getGameAddons().stream()
                                                                                                      .map(GameAddon::getName)
                                                                                                      .collect(Collectors.joining(", ")), NamedTextColor.GOLD
        )));
        lore.add(Component.text("Counted: ", NamedTextColor.GRAY).append(Component.text(counted, NamedTextColor.GREEN)));
        lore.add(Component.empty());
        lore.addAll(getExtraLore());
        return lore;
    }

    public GameMap getMap() {
        return map;
    }

    public List<GameAddon> getGameAddons() {
        return gameAddons;
    }

    public abstract List<Component> getExtraLore();

    public void setGameAddons(List<GameAddon> gameAddons) {
        this.gameAddons = gameAddons;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
