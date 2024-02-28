package com.ebicep.warlords.database.repositories.games.pojos;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.achievements.Achievement;
import com.ebicep.warlords.achievements.types.TieredAchievements;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import com.ebicep.warlords.database.leaderboards.guilds.GuildLeaderboardManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.*;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.DateUtil;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.TriFunction;
import com.ebicep.warlords.util.warlords.Utils;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import me.filoghost.holographicdisplays.api.hologram.line.ClickableHologramLine;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

    public static final Location LAST_GAME_STATS_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, 26.5, 86, 184.5);
    public static final Location TOP_DAMAGE_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, 37.5, 88, 181.5);
    public static final Location TOP_HEALING_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, 32.5, 88, 188.5);
    public static final Location TOP_ABSORBED_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, 25.5, 88, 193.5);
    public static final Location TOP_DHP_PER_MINUTE_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, -3.5, 88, 184.5);
    public static final Location TOP_DAMAGE_ON_CARRIER_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, -14.5, 88, 181.5);
    public static final Location TOP_HEALING_ON_CARRIER_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, -9.5, 88, 188.5);
    public static final Location GAME_SWITCH_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, 16.5, 83, 184.55);
    public static final List<DatabaseGameBase> previousGames = new ArrayList<>();
    protected static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

    public static boolean addGame(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean updatePlayerStats) {
        try {
            if (!GameMode.isPvE(game.getGameMode())) {
                float highestDamage = game.warlordsPlayers()
                                          .max(Comparator.comparing((WarlordsPlayer wp) -> wp.getMinuteStats().total().getDamage()))
                                          .get()
                                          .getMinuteStats()
                                          .total()
                                          .getDamage();
                float highestHealing = game.warlordsPlayers()
                                           .max(Comparator.comparing((WarlordsPlayer wp) -> wp.getMinuteStats().total().getHealing()))
                                           .get()
                                           .getMinuteStats()
                                           .total()
                                           .getHealing();
                //checking for inflated stats
                if (highestDamage > 750000 || highestHealing > 750000) {
                    updatePlayerStats = false;
                    ChatUtils.MessageType.WARLORDS.sendMessage("NOT UPDATING PLAYER STATS - Game exceeds 750k damage / healing");
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
                    case DUEL, DEBUG, SIMULATION_TRIAL -> updatePlayerStats = false;
                }
            }

            //Any game with these game addons will not record player stats
            for (GameAddon addon : game.getAddons()) {
                if (!updatePlayerStats) {
                    break;
                }
                switch (addon) {
                    case CUSTOM_GAME, IMPOSTER_MODE, COOLDOWN_MODE, TRIPLE_HEALTH, INTERCHANGE_MODE -> {
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
                        .forEachOrdered(warlordsPlayer -> DatabaseManager.getPlayer(warlordsPlayer.getUuid(),
                                databasePlayer -> databasePlayer.addAchievements(warlordsPlayer.getAchievementsUnlocked())
                        ));
                    if (!GameMode.isPvE(game.getGameMode())) {
                        Warlords.newChain()
                                .async(() -> ChatUtils.MessageType.WARLORDS.sendMessage(DatabaseGameCTF.getWarlordsPlusEndGameStats(game)))
                                .execute();
                    }
                }
            }

            TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> createDatabaseGame = game.getGameMode().createDatabaseGame;
            if (createDatabaseGame == null) {
                ChatUtils.MessageType.GAME_SERVICE.sendMessage("Cannot add game to database - the collection has not been configured");
                return false;
            }
            DatabaseGameBase databaseGame = createDatabaseGame.apply(game, gameWinEvent, updatePlayerStats);
            if (databaseGame == null) {
                ChatUtils.MessageType.GAME_SERVICE.sendMessage("Cannot add game to database - null database game");
                return false;
            }

            if (previousGames.size() >= 10) {
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
                                    "This game was added to the database but player information remained the same"), NamedTextColor.GREEN)
            );
        } catch (Exception e) {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Error adding game to database");
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage(e);

            TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> createDatabaseGame = game.getGameMode().createDatabaseGame;
            if (createDatabaseGame == null) {
                ChatUtils.MessageType.GAME_SERVICE.sendMessage("Cannot add game to database - the collection has not been configured");
                return false;
            }
            DatabaseGameBase databaseGame = createDatabaseGame.apply(game, gameWinEvent, updatePlayerStats);
            Warlords.newChain()
                    .async(() -> DatabaseManager.gameService.createBackup(databaseGame))
                    .execute();
        }
        return updatePlayerStats;
    }

    public static void addGameToDatabase(DatabaseGameBase databaseGame, Player player) {
        if (DatabaseManager.gameService == null) {
            return;
        }
        try {
            GamesCollections collection = databaseGame.getGameMode().gamesCollections;
            databaseGame.gameAddons.remove(GameAddon.CUSTOM_GAME);
            //game in the database
            if (DatabaseManager.gameService.exists(databaseGame, collection)) {
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
                Warlords.newChain()
                        .delay(4, TimeUnit.SECONDS)
                        .async(() -> DatabaseManager.gameService.create(databaseGame, collection))
                        .sync(() -> {
                            for (PlayersCollections activeCollection : PlayersCollections.ACTIVE_COLLECTIONS) {
                                StatsLeaderboardManager.resetLeaderboards(activeCollection, false);
                            }
                            StatsLeaderboardManager.setLeaderboardHologramVisibilityToAll();
                        })
                        .execute();
                //}
            }
        } catch (Exception e) {
            Warlords.newChain()
                    .async(() -> DatabaseManager.gameService.createBackup(databaseGame))
                    .execute();
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage(e);
        }
    }

    public static void removeGameFromDatabase(DatabaseGameBase databaseGame, Player player) {
        if (DatabaseManager.gameService == null) {
            return;
        }
        GamesCollections collection = databaseGame.getGameMode().gamesCollections;
        //game in the database
        if (DatabaseManager.gameService.exists(databaseGame, collection)) {
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
    }

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

    public void setCounted(boolean counted) {
        this.counted = counted;
    }

    public static void updatePlayerStatsFromTeam(DatabaseGameBase databaseGame, DatabaseGamePlayerBase gamePlayer, int multiplier) {
        //ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Updating " + gamePlayer.getName() + " stats from team");
        for (PlayersCollections activeCollection : PlayersCollections.ACTIVE_COLLECTIONS) {
            if (!activeCollection.shouldUpdate(databaseGame.getExactDate())) {
                return; //Can return because if game is not in the same week then it will not be in the same day
            }
            DatabaseManager.updatePlayer(gamePlayer.getUuid(), activeCollection, databasePlayer -> {
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
            });
        }
    }

    public Instant getExactDate() {
        return exactDate;
    }

    public void setExactDate(Instant exactDate) {
        this.exactDate = exactDate;
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

    public static void setGameHologramVisibility(Player player) {
        StatsLeaderboardManager.validatePlayerHolograms(player);

        int selectedGame = StatsLeaderboardManager.PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId()).getGameHologram();
        for (int i = 0; i < previousGames.size(); i++) {
            List<Hologram> gameHolograms = previousGames.get(i).getHolograms();
            if (i == selectedGame) {
                gameHolograms.forEach(hologram -> hologram.getVisibilitySettings()
                                                          .setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
            } else {
                gameHolograms.forEach(hologram -> hologram.getVisibilitySettings()
                                                          .setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN));
            }
        }

        createGameSwitcherHologram(player);
    }

    private static void createGameSwitcherHologram(Player player) {
        HolographicDisplaysAPI.get(Warlords.getInstance()).getHolograms().stream()
                              .filter(h -> h.getVisibilitySettings().isVisibleTo(player) && h.getPosition()
                                                                                             .toLocation()
                                                                                             .equals(DatabaseGameBase.GAME_SWITCH_LOCATION))
                              .forEach(Hologram::delete);

        Hologram gameSwitcher = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.GAME_SWITCH_LOCATION);
        gameSwitcher.getLines().appendText(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + "Last " + previousGames.size() + " Games");
        gameSwitcher.getLines().appendText("");

        PlayerLeaderboardInfo playerLeaderboardInfo = StatsLeaderboardManager.PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId());
        int selectedGame = playerLeaderboardInfo.getGameHologram();
        int gameBefore = getGameBefore(selectedGame);
        int gameAfter = getGameAfter(selectedGame);

        if (previousGames.isEmpty()) {
            return;
        }

        ClickableHologramLine beforeLine;
        ClickableHologramLine afterLine;
        if (previousGames.size() > 1) {
            if (gameBefore == previousGames.size() - 1) {
                beforeLine = gameSwitcher.getLines().appendText(ChatColor.GRAY + "Latest Game");
            } else {
                beforeLine = gameSwitcher.getLines()
                                         .appendText(ChatColor.GRAY.toString() + (gameBefore + 1) + ". " + previousGames.get(gameBefore).getDate());
            }
            beforeLine.setClickListener((clicker) -> {
                playerLeaderboardInfo.setGameHologram(gameBefore);
                setGameHologramVisibility(player);
            });
        }
        if (selectedGame == previousGames.size() - 1) {
            gameSwitcher.getLines().appendText(ChatColor.GREEN + "Latest Game");
        } else {
            gameSwitcher.getLines()
                        .appendText(ChatColor.GREEN.toString() + (selectedGame + 1) + ". " + previousGames.get(selectedGame).getDate());
        }
        if (previousGames.size() > 2) {
            if (gameAfter == previousGames.size() - 1) {
                afterLine = gameSwitcher.getLines().appendText(ChatColor.GRAY + "Latest Game");
            } else {
                afterLine = gameSwitcher.getLines()
                                        .appendText(ChatColor.GRAY.toString() + (gameAfter + 1) + ". " + previousGames.get(gameAfter).getDate());
            }
            afterLine.setClickListener((clicker) -> {
                playerLeaderboardInfo.setGameHologram(gameAfter);
                setGameHologramVisibility(player);
            });
        }
        gameSwitcher.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        gameSwitcher.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);
    }

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

    public abstract Set<T> getBasePlayers();

    public abstract DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player);

    public void createHolograms() {
        List<Hologram> holograms = new ArrayList<>();

        //readding game holograms
        Hologram lastGameStats = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.LAST_GAME_STATS_LOCATION);
        holograms.add(lastGameStats);
        lastGameStats.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Last " + (isPrivate() ? "Private" : "Pub") + " Game Stats");

        Hologram topDamage = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.TOP_DAMAGE_LOCATION);
        holograms.add(topDamage);
        topDamage.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Damage");

        Hologram topHealing = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.TOP_HEALING_LOCATION);
        holograms.add(topHealing);
        topHealing.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Healing");

        Hologram topAbsorbed = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.TOP_ABSORBED_LOCATION);
        holograms.add(topAbsorbed);
        topAbsorbed.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Absorbed");

        Hologram topDHPPerMinute = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.TOP_DHP_PER_MINUTE_LOCATION);
        holograms.add(topDHPPerMinute);
        topDHPPerMinute.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top DHP per Minute");

        //last game stats
        appendLastGameStats(lastGameStats);

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
            StringBuilder playerSpecs = new StringBuilder(ChatColor.AQUA + s).append(": ");
            final boolean[] add = {false};
            allPlayers.stream()
                      .filter(o -> o.getSpec().name.equalsIgnoreCase(s))
                      .sorted((o1, o2) -> Integer.compare(getTeam(o2).ordinal(), getTeam(o1).ordinal()))
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
                players.add(playerSpecs.toString());
            }
        }
        players.forEach(s -> lastGameStats.getLines().appendText(s));

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
                      topDamagePlayers.add(playerColor.get(databaseGamePlayer) + databaseGamePlayer.getName() + ": " +
                              ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalDamage()));
                  });

        allPlayers.stream()
                  .sorted(Comparator.comparingLong(DatabaseGamePlayerBase::getTotalHealing).reversed())
                  .forEach(databaseGamePlayer -> {
                      totalHealing.merge(playerColor.get(databaseGamePlayer), databaseGamePlayer.getTotalHealing(), Long::sum);
                      topHealingPlayers.add(playerColor.get(databaseGamePlayer) + databaseGamePlayer.getName() + ": " +
                              ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalHealing()));
                  });

        allPlayers.stream()
                  .sorted(Comparator.comparingLong(DatabaseGamePlayerBase::getTotalAbsorbed).reversed())
                  .forEach(databaseGamePlayer -> {
                      totalAbsorbed.merge(playerColor.get(databaseGamePlayer), databaseGamePlayer.getTotalAbsorbed(), Long::sum);
                      topAbsorbedPlayers.add(playerColor.get(databaseGamePlayer) + databaseGamePlayer.getName() + ": " +
                              ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalAbsorbed()));
                  });

        appendTeamDHP(topDamage, totalDamage);
        appendTeamDHP(topHealing, totalHealing);
        appendTeamDHP(topAbsorbed, totalAbsorbed);

        topDamagePlayers.forEach(s -> topDamage.getLines().appendText(s));
        topHealingPlayers.forEach(s -> topHealing.getLines().appendText(s));
        topAbsorbedPlayers.forEach(s -> topAbsorbed.getLines().appendText(s));

        addCustomHolograms(holograms);

        //setting visibility to none
        holograms.forEach(hologram -> {
            hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        });

        this.holograms = holograms;
    }

    public abstract void appendLastGameStats(Hologram hologram);

    public abstract void addCustomHolograms(List<Hologram> holograms);

    public abstract String getGameLabel();

    public abstract Team getTeam(DatabaseGamePlayerBase player);

    protected void appendTeamDHP(Hologram hologram, Map<ChatColor, Long> map) {
        map.entrySet().stream().sorted(Map.Entry.<ChatColor, Long>comparingByValue().reversed()).forEach(chatColorLongEntry -> {
            ChatColor key = chatColorLongEntry.getKey();
            Long value = chatColorLongEntry.getValue();
            hologram.getLines().appendText(key + (key == ChatColor.BLUE ? "Blue: " : "Red: ") + ChatColor.YELLOW + NumberFormat.addCommaAndRound(value));
        });
    }

    public List<Component> getLore() {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Map: ", NamedTextColor.GRAY).append(Component.text(getMap().getMapName(), NamedTextColor.YELLOW)));
        lore.add(Component.text("Mode: ", NamedTextColor.GRAY).append(Component.text(getGameMode().getName(), NamedTextColor.AQUA)));
        lore.add(Component.text("Addons: ", NamedTextColor.GRAY).append(Component.text(getGameAddons().stream()
                                                                                                      .map(GameAddon::getName)
                                                                                                      .collect(Collectors.joining(", ")), NamedTextColor.GOLD)));
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

    public boolean isPrivate() {
        return gameAddons.contains(GameAddon.PRIVATE_GAME);
    }

    public List<Hologram> getHolograms() {
        if (holograms.isEmpty()) {
            createHolograms();
        }
        return holograms;
    }

    public void deleteHolograms() {
        holograms.forEach(Hologram::delete);
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
