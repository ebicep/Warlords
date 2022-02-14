package com.ebicep.warlords.database.repositories.games.pojos;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.LeaderboardManager;
import com.ebicep.warlords.database.repositories.games.GameMode;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.WinAfterTimeoutOption;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.NumberFormat;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import me.filoghost.holographicdisplays.api.beta.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.beta.hologram.Hologram;
import me.filoghost.holographicdisplays.api.beta.hologram.VisibilitySettings;
import me.filoghost.holographicdisplays.api.beta.hologram.line.ClickableHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


@Document(collection = "Games_Information")
public class DatabaseGame {

    @Id
    protected String id;

    protected String date;
    protected String map;
    @Field("time_left")
    protected int timeLeft;
    protected String winner;
    @Field("blue_points")
    protected int bluePoints;
    @Field("red_points")
    protected int redPoints;
    protected DatabaseGamePlayers players;
    protected String statInfo;
    @Field("gamemode")
    protected GameMode gameMode;
    @Field("private")
    protected boolean isPrivate = true;
    protected boolean counted;

    public DatabaseGame() {

    }

    public DatabaseGame(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
        Team winner = gameWinEvent == null || gameWinEvent.isCancelled() ? null : gameWinEvent.getDeclaredWinner();
        List<DatabaseGamePlayers.GamePlayer> blue = new ArrayList<>();
        List<DatabaseGamePlayers.GamePlayer> red = new ArrayList<>();
        for (WarlordsPlayer warlordsPlayer : PlayerFilter.playingGame(game)) {
            if (warlordsPlayer.getTeam() == Team.BLUE) {
                blue.add(new DatabaseGamePlayers.GamePlayer(warlordsPlayer));
            } else if (warlordsPlayer.getTeam() == Team.RED) {
                red.add(new DatabaseGamePlayers.GamePlayer(warlordsPlayer));
            }
        }
        this.date = dateFormat.format(new Date());
        this.map = game.getMap().getMapName();
        this.timeLeft = WinAfterTimeoutOption.getTimeLeft(game).orElse(-1);
        this.winner = winner == null ? "DRAW" : winner.getName().toUpperCase(Locale.ROOT);
        // TODO add suport here for multiple teams and rememeber that not every game has a red and blue team
        this.bluePoints = game.getPoints(Team.BLUE);
        this.redPoints = game.getPoints(Team.RED);
        this.players = new DatabaseGamePlayers(blue, red);
        this.statInfo = getWarlordsPlusEndGameStats(game);
        this.isPrivate = game.getAddons().contains(GameAddon.PRIVATE_GAME);
        this.counted = counted;
    }

    @Override
    public String toString() {
        return "Game{" +
                "date='" + date + '\'' +
                ", map='" + map + '\'' +
                '}';
    }

    @Transient
    private List<Hologram> holograms = new ArrayList<>();
    @Transient
    public static final Location lastGameStatsLocation = new Location(LeaderboardManager.world, -2532.5, 56, 766.5);
    @Transient
    public static final Location topDamageLocation = new Location(LeaderboardManager.world, -2540.5, 58, 785.5);
    @Transient
    public static final Location topHealingLocation = new Location(LeaderboardManager.world, -2546.5, 58, 785.5);
    @Transient
    public static final Location topAbsorbedLocation = new Location(LeaderboardManager.world, -2552.5, 58, 785.5);
    @Transient
    public static final Location topDHPPerMinuteLocation = new Location(LeaderboardManager.world, -2530.5, 59.5, 781.5);
    @Transient
    public static final Location topDamageOnCarrierLocation = new Location(LeaderboardManager.world, -2572.5, 58, 778.5);
    @Transient
    public static final Location topHealingOnCarrierLocation = new Location(LeaderboardManager.world, -2579.5, 58, 774.5);
    @Transient
    public static final Location gameSwitchLocation = new Location(LeaderboardManager.world, -2543.5, 53.5, 769.5);

    @Transient
    public static List<DatabaseGame> previousGames = new ArrayList<>();

    public void createHolograms() {
        List<Hologram> holograms = new ArrayList<>();

        //readding game holograms
        Hologram lastGameStats = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGame.lastGameStatsLocation);
        holograms.add(lastGameStats);
        lastGameStats.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Last " + (isPrivate ? "Comp" : "Pub") + " Game Stats");

        Hologram topDamage = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGame.topDamageLocation);
        holograms.add(topDamage);
        topDamage.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Damage");

        Hologram topHealing = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGame.topHealingLocation);
        holograms.add(topHealing);
        topHealing.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Healing");

        Hologram topAbsorbed = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGame.topAbsorbedLocation);
        holograms.add(topAbsorbed);
        topAbsorbed.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Absorbed");

        Hologram topDHPPerMinute = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGame.topDHPPerMinuteLocation);
        holograms.add(topDHPPerMinute);
        topDHPPerMinute.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top DHP per Minute");

        Hologram topDamageOnCarrier = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGame.topDamageOnCarrierLocation);
        holograms.add(topDamageOnCarrier);
        topDamageOnCarrier.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Damage On Carrier");

        Hologram topHealingOnCarrier = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGame.topHealingOnCarrierLocation);
        holograms.add(topHealingOnCarrier);
        topHealingOnCarrier.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Healing On Carrier");

        //last game stats
        int timeLeft = getTimeLeft();
        int minutes = (15 - (int) Math.round(timeLeft / 60.0)) == 0 ? 1 : 15 - (int) Math.round(timeLeft / 60.0);
        lastGameStats.getLines().appendText(ChatColor.GRAY + getDate());
        lastGameStats.getLines().appendText(ChatColor.GREEN + getMap() + ChatColor.GRAY + "  -  " + ChatColor.GREEN + timeLeft / 60 + ":" + timeLeft % 60 + (timeLeft % 60 < 10 ? "0" : ""));
        lastGameStats.getLines().appendText(ChatColor.BLUE.toString() + getBluePoints() + ChatColor.GRAY + "  -  " + ChatColor.RED + getRedPoints());


        List<DatabaseGamePlayers.GamePlayer> bluePlayers = players.blue;
        List<DatabaseGamePlayers.GamePlayer> redPlayers = players.red;
        List<DatabaseGamePlayers.GamePlayer> allPlayers = new ArrayList<>();
        allPlayers.addAll(bluePlayers);
        allPlayers.addAll(redPlayers);
        List<String> players = new ArrayList<>();

        for (String s : Utils.specsOrdered) {
            StringBuilder playerSpecs = new StringBuilder(ChatColor.AQUA + s).append(": ");
            final boolean[] add = {false};
            allPlayers.stream().filter(o -> o.getSpec().name.equalsIgnoreCase(s)).forEach(p -> {
                playerSpecs.append(bluePlayers.contains(p) ? ChatColor.BLUE : ChatColor.RED).append(p.getName()).append(p.getKDAString()).append(ChatColor.GRAY).append(", ");
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
        List<String> topDHPPerGamePlayers = new ArrayList<>();
        List<String> topDamageOnCarrierPlayers = new ArrayList<>();
        List<String> topHealingOnCarrierPlayers = new ArrayList<>();

        Map<ChatColor, Long> totalDamage = new HashMap<>();
        Map<ChatColor, Long> totalHealing = new HashMap<>();
        Map<ChatColor, Long> totalAbsorbed = new HashMap<>();

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayers.GamePlayer::getTotalDamage).reversed()).forEach(databaseGamePlayer -> {
            totalDamage.put(bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED, totalDamage.getOrDefault(bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED, 0L) + databaseGamePlayer.getTotalDamage());
            topDamagePlayers.add((bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED) + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalDamage()));
        });

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayers.GamePlayer::getTotalHealing).reversed()).forEach(databaseGamePlayer -> {
            totalHealing.put(bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED, totalHealing.getOrDefault(bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED, 0L) + databaseGamePlayer.getTotalHealing());
            topHealingPlayers.add((bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED) + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalHealing()));
        });

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayers.GamePlayer::getTotalAbsorbed).reversed()).forEach(databaseGamePlayer -> {
            totalAbsorbed.put(bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED, totalAbsorbed.getOrDefault(bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED, 0L) + databaseGamePlayer.getTotalAbsorbed());
            topAbsorbedPlayers.add((bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED) + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalAbsorbed()));
        });

        allPlayers.stream().sorted((o1, o2) -> {
            Long p1DHPPerGame = o1.getTotalDHP() / minutes;
            Long p2DHPPerGame = o2.getTotalDHP() / minutes;
            return p2DHPPerGame.compareTo(p1DHPPerGame);
        }).forEach(databaseGamePlayer -> {
            topDHPPerGamePlayers.add((bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED) + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalDHP() / minutes));
        });

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayers.GamePlayer::getTotalDamageOnCarrier).reversed()).forEach(databaseGamePlayer -> {
            topDamageOnCarrierPlayers.add((bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED) + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalDamageOnCarrier()));
        });

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayers.GamePlayer::getTotalHealingOnCarrier).reversed()).forEach(databaseGamePlayer -> {
            topHealingOnCarrierPlayers.add((bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED) + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalHealingOnCarrier()));
        });

        appendTeamDHP(topDamage, totalDamage);
        appendTeamDHP(topHealing, totalHealing);
        appendTeamDHP(topAbsorbed, totalAbsorbed);

        topDamagePlayers.forEach(s -> topDamage.getLines().appendText(s));
        topHealingPlayers.forEach(s -> topHealing.getLines().appendText(s));
        topAbsorbedPlayers.forEach(s -> topAbsorbed.getLines().appendText(s));
        topDHPPerGamePlayers.forEach(s -> topDHPPerMinute.getLines().appendText(s));
        topDamageOnCarrierPlayers.forEach(s -> topDamageOnCarrier.getLines().appendText(s));
        topHealingOnCarrierPlayers.forEach(s -> topHealingOnCarrier.getLines().appendText(s));

        //setting visibility to none
        holograms.forEach(hologram -> {
            hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        });

        this.holograms = holograms;
    }

    private void appendTeamDHP(Hologram hologram, Map<ChatColor, Long> map) {
        map.entrySet().stream().sorted(Map.Entry.<ChatColor, Long>comparingByValue().reversed()).forEach(chatColorLongEntry -> {
            ChatColor key = chatColorLongEntry.getKey();
            Long value = chatColorLongEntry.getValue();
            hologram.getLines().appendText(key + (key == ChatColor.BLUE ? "Blue: " : "Red: ") + ChatColor.YELLOW + NumberFormat.addCommaAndRound(value));
        });
    }

    public static void setGameHologramVisibility(Player player) {
        if (!LeaderboardManager.playerGameHolograms.containsKey(player.getUniqueId()) ||
                LeaderboardManager.playerGameHolograms.get(player.getUniqueId()) == null ||
                LeaderboardManager.playerGameHolograms.get(player.getUniqueId()) < 0
        ) {
            LeaderboardManager.playerGameHolograms.put(player.getUniqueId(), previousGames.size() - 1);
        }
        int selectedGame = LeaderboardManager.playerGameHolograms.get(player.getUniqueId());
        for (int i = 0; i < previousGames.size(); i++) {
            List<Hologram> gameHolograms = previousGames.get(i).getHolograms();
            if (i == selectedGame) {
                gameHolograms.forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
            } else {
                gameHolograms.forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN));
            }
        }

        createGameSwitcherHologram(player);
    }

    private static void createGameSwitcherHologram(Player player) {
        HolographicDisplaysAPI.get(Warlords.getInstance()).getHolograms().stream()
                .filter(h -> h.getVisibilitySettings().isVisibleTo(player) && h.getPosition().toLocation().equals(DatabaseGame.gameSwitchLocation))
                .forEach(Hologram::delete);
        
        Hologram gameSwitcher = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGame.gameSwitchLocation);
        gameSwitcher.getLines().appendText(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + "Last " + previousGames.size() + " Games");
        gameSwitcher.getLines().appendText("");

        int selectedGame = LeaderboardManager.playerGameHolograms.get(player.getUniqueId());
        int gameBefore = getGameBefore(selectedGame);
        int gameAfter = getGameAfter(selectedGame);

        ClickableHologramLine beforeLine;
        ClickableHologramLine afterLine;
        if (gameBefore == previousGames.size() - 1) {
            beforeLine = gameSwitcher.getLines().appendText(ChatColor.GRAY + "Latest Game");
        } else {
            beforeLine = gameSwitcher.getLines().appendText(ChatColor.GRAY.toString() + (gameBefore + 1) + ". " + previousGames.get(gameBefore).getDate());
        }
        if (selectedGame == previousGames.size() - 1) {
            gameSwitcher.getLines().appendText(ChatColor.GREEN + "Latest Game");
        } else {
            gameSwitcher.getLines().appendText(ChatColor.GREEN.toString() + (selectedGame + 1) + ". " + previousGames.get(selectedGame).getDate());
        }

        if (gameAfter == previousGames.size() - 1) {
            afterLine = gameSwitcher.getLines().appendText(ChatColor.GRAY + "Latest Game");
        } else {
            afterLine = gameSwitcher.getLines().appendText(ChatColor.GRAY.toString() + (gameAfter + 1) + ". " + previousGames.get(gameAfter).getDate());
        }

        beforeLine.setClickListener((clicker) -> {
            LeaderboardManager.playerGameHolograms.put(player.getUniqueId(), gameBefore);
            setGameHologramVisibility(player);
        });

        afterLine.setClickListener((clicker) -> {
            LeaderboardManager.playerGameHolograms.put(player.getUniqueId(), gameAfter);
            setGameHologramVisibility(player);
        });

        gameSwitcher.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        gameSwitcher.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);
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

    public List<Hologram> getHolograms() {
        if (holograms.isEmpty()) {
            createHolograms();
        }
        return holograms;
    }

    public void deleteHolograms() {
        holograms.forEach(Hologram::delete);
    }

    public static void addGame(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean updatePlayerStats) {
        try {
            previousGames.get(0).deleteHolograms();
            previousGames.remove(0);
            DatabaseGame databaseGame = new DatabaseGame(game, gameWinEvent, updatePlayerStats);
            previousGames.add(databaseGame);
            databaseGame.createHolograms();

            //if (databaseGame.isPrivate) {
                addGameToDatabase(databaseGame);

            LeaderboardManager.addHologramLeaderboards(UUID.randomUUID().toString(), false);

                //sending message if player information remained the same
                for (WarlordsPlayer value : PlayerFilter.playingGame(game)) {
                    if (value.getEntity().hasPermission("warlords.database.messagefeed")) {
                        if (updatePlayerStats) {
                            value.sendMessage(ChatColor.GREEN + "This game was added to the database and player information was updated");
                        } else {
                            value.sendMessage(ChatColor.GREEN + "This game was added to the database but player information remained the same");
                        }
                    }
                }
            //}
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR TRYING TO ADD GAME");
        }

    }

    public static void addGameToDatabase(DatabaseGame databaseGame) {
        //game in the database
        if (DatabaseManager.gameService.exists(databaseGame)) {
            //if not counted then update player stats then set counted to true, else do nothing
            if (!databaseGame.isCounted()) {
                updatePlayerStatsFromGame(databaseGame,true);
                databaseGame.setCounted(true);
                DatabaseManager.updateGameAsync(databaseGame);
            }
        } else {
            //game not in database then add game and update player stats if counted
            if (databaseGame.isCounted()) {
                updatePlayerStatsFromGame(databaseGame, true);
            }
            //only add game if comps
            //if (databaseGame.isPrivate) {
            Warlords.newChain()
                    .async(() -> DatabaseManager.gameService.create(databaseGame))
                    .sync(() -> {
                        LeaderboardManager.playerGameHolograms.forEach((uuid, integer) -> {
                            LeaderboardManager.playerGameHolograms.put(uuid, previousGames.size() - 1);
                            if (Bukkit.getPlayer(uuid) != null) {
                                setGameHologramVisibility(Bukkit.getPlayer(uuid));
                            }
                        });
                    })
                    .execute();
            //}
        }
    }

    public static void removeGameFromDatabase(DatabaseGame databaseGame) {
        //game in the database
        if (DatabaseManager.gameService.exists(databaseGame)) {
            //if counted then remove player stats then set counted to false, else do nothing
            if (databaseGame.isCounted()) {
                updatePlayerStatsFromGame(databaseGame,false);
                databaseGame.setCounted(false);
                DatabaseManager.updateGameAsync(databaseGame);
            }
        }
        //else game not in database then do nothing
    }

    private static void updatePlayerStatsFromGame(DatabaseGame databaseGame, boolean add) {
        databaseGame.getPlayers().getBlue().forEach(gamePlayer -> updatePlayerStatsFromTeam(databaseGame, add, gamePlayer, true));
        databaseGame.getPlayers().getRed().forEach(gamePlayer -> updatePlayerStatsFromTeam(databaseGame, add, gamePlayer, false));
    }

    private static void updatePlayerStatsFromTeam(DatabaseGame databaseGame, boolean add, DatabaseGamePlayers.GamePlayer gamePlayer, boolean blue) {
        DatabasePlayer databasePlayerAllTime = DatabaseManager.playerService.findByUUID(UUID.fromString(gamePlayer.getUuid()));
        DatabasePlayer databasePlayerSeason = DatabaseManager.playerService.findByUUID(UUID.fromString(gamePlayer.getUuid()), PlayersCollections.SEASON_5);
        DatabasePlayer databasePlayerWeekly = DatabaseManager.playerService.findByUUID(UUID.fromString(gamePlayer.getUuid()), PlayersCollections.WEEKLY);
        DatabasePlayer databasePlayerDaily = DatabaseManager.playerService.findByUUID(UUID.fromString(gamePlayer.getUuid()), PlayersCollections.DAILY);

        if (databasePlayerAllTime != null) {
            updatePlayerStats(databaseGame, add, gamePlayer, databasePlayerAllTime, blue);
            DatabaseManager.updatePlayerAsync(databasePlayerAllTime);
        } else System.out.println("WARNING - " + gamePlayer.getName() + " was not found in ALL_TIME");
        if (databasePlayerSeason != null) {
            updatePlayerStats(databaseGame, add, gamePlayer, databasePlayerSeason, blue);
            DatabaseManager.updatePlayerAsync(databasePlayerSeason, PlayersCollections.SEASON_5);
        } else System.out.println("WARNING - " + gamePlayer.getName() + " was not found in SEASON");
        if (databasePlayerWeekly != null) {
            updatePlayerStats(databaseGame, add, gamePlayer, databasePlayerWeekly, blue);
            DatabaseManager.updatePlayerAsync(databasePlayerWeekly, PlayersCollections.WEEKLY);
        } else System.out.println("WARNING - " + gamePlayer.getName() + " was not found in WEEKLY");
        if (databasePlayerDaily != null) {
            updatePlayerStats(databaseGame, add, gamePlayer, databasePlayerDaily, blue);
            DatabaseManager.updatePlayerAsync(databasePlayerDaily, PlayersCollections.DAILY);
        } else System.out.println("WARNING - " + gamePlayer.getName() + " was not found in DAILY");
    }

    private static void updatePlayerStats(DatabaseGame databaseGame, boolean add, DatabaseGamePlayers.GamePlayer gamePlayer, DatabasePlayer databasePlayer, boolean checkBlueWin) {
        boolean won = checkBlueWin ? databaseGame.bluePoints > databaseGame.redPoints : databaseGame.redPoints > databaseGame.bluePoints;
        databasePlayer.updateStats(GameMode.CAPTURE_THE_FLAG, databaseGame.isPrivate, databaseGame, gamePlayer, won, add);
    }

    @Transient
    public static String lastWarlordsPlusString = "";

    public static String getWarlordsPlusEndGameStats(Game game) {
        StringBuilder output = new StringBuilder("Winners:");
        int bluePoints = game.getPoints(Team.BLUE);
        int redPoints = game.getPoints(Team.RED);
        if (bluePoints > redPoints) {
            for (WarlordsPlayer player : PlayerFilter.playingGame(game).matchingTeam(Team.BLUE)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getStats().total().getKills()).append(":").append(player.getStats().total().getDeaths()).append("],");
            }
            output.setLength(output.length() - 1);
            output.append("Losers:");
            for (WarlordsPlayer player : PlayerFilter.playingGame(game).matchingTeam(Team.RED)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getStats().total().getKills()).append(":").append(player.getStats().total().getDeaths()).append("],");
            }
        } else if (redPoints > bluePoints) {
            for (WarlordsPlayer player : PlayerFilter.playingGame(game).matchingTeam(Team.RED)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getStats().total().getKills()).append(":").append(player.getStats().total().getDeaths()).append("],");
            }
            output.setLength(output.length() - 1);
            output.append("Losers:");
            for (WarlordsPlayer player : PlayerFilter.playingGame(game).matchingTeam(Team.BLUE)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getStats().total().getKills()).append(":").append(player.getStats().total().getDeaths()).append("],");
            }
        } else {
            output.setLength(0);
            for (WarlordsPlayer player : PlayerFilter.playingGame(game).matchingTeam(Team.BLUE)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getStats().total().getKills()).append(":").append(player.getStats().total().getDeaths()).append("],");
            }
            for (WarlordsPlayer player : PlayerFilter.playingGame(game).matchingTeam(Team.RED)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getStats().total().getKills()).append(":").append(player.getStats().total().getDeaths()).append("],");
            }
        }
        output.setLength(output.length() - 1);
        if (BotManager.numberOfMessagesSentLast30Sec > 15) {
            if (BotManager.numberOfMessagesSentLast30Sec < 20) {
                BotManager.getTextChannelCompsByName("games-backlog").ifPresent(textChannel -> textChannel.sendMessage("SOMETHING BROKEN DETECTED <@239929120035700737> <@253971614998331393>").queue());
            }
        } else {
            BotManager.getTextChannelCompsByName("games-backlog").ifPresent(textChannel -> textChannel.sendMessage(output.toString()).queue());
        }
        lastWarlordsPlusString = output.toString();
        return output.toString();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getBluePoints() {
        return bluePoints;
    }

    public void setBluePoints(int bluePoints) {
        this.bluePoints = bluePoints;
    }

    public int getRedPoints() {
        return redPoints;
    }

    public void setRedPoints(int redPoints) {
        this.redPoints = redPoints;
    }

    public DatabaseGamePlayers getPlayers() {
        return players;
    }

    public void setPlayers(DatabaseGamePlayers players) {
        this.players = players;
    }

    public String getStatInfo() {
        return statInfo;
    }

    public void setStatInfo(String statInfo) {
        this.statInfo = statInfo;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isCounted() {
        return counted;
    }

    public void setCounted(boolean counted) {
        this.counted = counted;
    }

    public String getGameLabel() {
        return ChatColor.GRAY + date + ChatColor.DARK_GRAY + " - " +
                ChatColor.GREEN + map + ChatColor.DARK_GRAY + " - " +
                ChatColor.GRAY + "(" + ChatColor.BLUE + bluePoints + ChatColor.GRAY + ":" + ChatColor.RED + redPoints + ChatColor.GRAY + ")" + ChatColor.DARK_GRAY + " - " + ChatColor.DARK_PURPLE + isCounted();
    }
}
