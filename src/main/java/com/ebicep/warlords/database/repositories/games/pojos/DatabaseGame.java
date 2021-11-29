package com.ebicep.warlords.database.repositories.games.pojos;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.leaderboards.LeaderboardManager;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.state.PlayingState;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.query.Criteria;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


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
    protected boolean counted;

    public DatabaseGame() {

    }

    public DatabaseGame(PlayingState gameState, boolean counted) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
        Team winner = gameState.calculateWinnerByPoints();
        List<DatabaseGamePlayers.GamePlayer> blue = new ArrayList<>();
        List<DatabaseGamePlayers.GamePlayer> red = new ArrayList<>();
        for (WarlordsPlayer warlordsPlayer : PlayerFilter.playingGame(gameState.getGame())) {
            if (warlordsPlayer.getTeam() == Team.BLUE) {
                blue.add(new DatabaseGamePlayers.GamePlayer(warlordsPlayer));
            } else if (warlordsPlayer.getTeam() == Team.RED) {
                red.add(new DatabaseGamePlayers.GamePlayer(warlordsPlayer));
            }
        }
        this.date = dateFormat.format(new Date());
        this.map = gameState.getGame().getMap().getMapName();
        this.timeLeft = gameState.getTimerInSeconds();
        this.winner = gameState.isForceEnd() || winner == null ? "DRAW" : winner.name.toUpperCase(Locale.ROOT);
        this.bluePoints = gameState.getStats(Team.BLUE).points();
        this.redPoints = gameState.getStats(Team.RED).points();
        this.players = new DatabaseGamePlayers(blue, red);
        this.statInfo = getWarlordsPlusEndGameStats(gameState);
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
    public static final String[] specsOrdered = {"Pyromancer", "Cryomancer", "Aquamancer", "Berserker", "Defender", "Revenant", "Avenger", "Crusader", "Protector", "Thunderlord", "Spiritguard", "Earthwarden"};
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
    public static final Location topDHPPerMinuteLocation = new Location(LeaderboardManager.world, -2530.5, 59, 781.5);
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
        Hologram lastGameStats = HologramsAPI.createHologram(Warlords.getInstance(), DatabaseGame.lastGameStatsLocation);
        holograms.add(lastGameStats);
        lastGameStats.appendTextLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Last Game Stats");

        Hologram topDamage = HologramsAPI.createHologram(Warlords.getInstance(), DatabaseGame.topDamageLocation);
        holograms.add(topDamage);
        topDamage.appendTextLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Damage");

        Hologram topHealing = HologramsAPI.createHologram(Warlords.getInstance(), DatabaseGame.topHealingLocation);
        holograms.add(topHealing);
        topHealing.appendTextLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Healing");

        Hologram topAbsorbed = HologramsAPI.createHologram(Warlords.getInstance(), DatabaseGame.topAbsorbedLocation);
        holograms.add(topAbsorbed);
        topAbsorbed.appendTextLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Absorbed");

        Hologram topDHPPerMinute = HologramsAPI.createHologram(Warlords.getInstance(), DatabaseGame.topDHPPerMinuteLocation);
        holograms.add(topDHPPerMinute);
        topDHPPerMinute.appendTextLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top DHP per Minute");

        Hologram topDamageOnCarrier = HologramsAPI.createHologram(Warlords.getInstance(), DatabaseGame.topDamageOnCarrierLocation);
        holograms.add(topDamageOnCarrier);
        topDamageOnCarrier.appendTextLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Damage On Carrier");

        Hologram topHealingOnCarrier = HologramsAPI.createHologram(Warlords.getInstance(), DatabaseGame.topHealingOnCarrierLocation);
        holograms.add(topHealingOnCarrier);
        topHealingOnCarrier.appendTextLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Healing On Carrier");

        //last game stats
        int timeLeft = getTimeLeft();
        int minutes = (15 - (int) Math.round(timeLeft / 60.0)) == 0 ? 1 : 15 - (int) Math.round(timeLeft / 60.0);
        lastGameStats.appendTextLine(ChatColor.GRAY + getDate());
        lastGameStats.appendTextLine(ChatColor.GREEN + getMap() + ChatColor.GRAY + "  -  " + ChatColor.GREEN + timeLeft / 60 + ":" + timeLeft % 60 + (timeLeft % 60 < 10 ? "0" : ""));
        lastGameStats.appendTextLine(ChatColor.BLUE.toString() + getBluePoints() + ChatColor.GRAY + "  -  " + ChatColor.RED + getRedPoints());


        List<DatabaseGamePlayers.GamePlayer> bluePlayers = players.blue;
        List<DatabaseGamePlayers.GamePlayer> redPlayers = players.red;
        List<DatabaseGamePlayers.GamePlayer> allPlayers = new ArrayList<>();
        allPlayers.addAll(bluePlayers);
        allPlayers.addAll(redPlayers);
        List<String> players = new ArrayList<>();

        for (String s : specsOrdered) {
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
        players.forEach(lastGameStats::appendTextLine);

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
            topDamagePlayers.add((bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED) + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + Utils.addCommaAndRound(databaseGamePlayer.getTotalDamage()));
        });

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayers.GamePlayer::getTotalHealing).reversed()).forEach(databaseGamePlayer -> {
            totalHealing.put(bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED, totalHealing.getOrDefault(bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED, 0L) + databaseGamePlayer.getTotalHealing());
            topHealingPlayers.add((bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED) + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + Utils.addCommaAndRound(databaseGamePlayer.getTotalHealing()));
        });

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayers.GamePlayer::getTotalAbsorbed).reversed()).forEach(databaseGamePlayer -> {
            totalAbsorbed.put(bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED, totalAbsorbed.getOrDefault(bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED, 0L) + databaseGamePlayer.getTotalAbsorbed());
            topAbsorbedPlayers.add((bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED) + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + Utils.addCommaAndRound(databaseGamePlayer.getTotalAbsorbed()));
        });

        allPlayers.stream().sorted((o1, o2) -> {
            Long p1DHPPerGame = o1.getTotalDHP() / minutes;
            Long p2DHPPerGame = o2.getTotalDHP() / minutes;
            return p2DHPPerGame.compareTo(p1DHPPerGame);
        }).forEach(databaseGamePlayer -> {
            topDHPPerGamePlayers.add((bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED) + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + Utils.addCommaAndRound(databaseGamePlayer.getTotalDHP() / minutes));
        });

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayers.GamePlayer::getTotalDamageOnCarrier).reversed()).forEach(databaseGamePlayer -> {
            topDamageOnCarrierPlayers.add((bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED) + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + Utils.addCommaAndRound(databaseGamePlayer.getTotalDamageOnCarrier()));
        });

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayers.GamePlayer::getTotalHealingOnCarrier).reversed()).forEach(databaseGamePlayer -> {
            topHealingOnCarrierPlayers.add((bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED) + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + Utils.addCommaAndRound(databaseGamePlayer.getTotalHealingOnCarrier()));
        });

        appendTeamDHP(topDamage, totalDamage);
        appendTeamDHP(topHealing, totalHealing);
        appendTeamDHP(topAbsorbed, totalAbsorbed);

        topDamagePlayers.forEach(topDamage::appendTextLine);
        topHealingPlayers.forEach(topHealing::appendTextLine);
        topAbsorbedPlayers.forEach(topAbsorbed::appendTextLine);
        topDHPPerGamePlayers.forEach(topDHPPerMinute::appendTextLine);
        topDamageOnCarrierPlayers.forEach(topDamageOnCarrier::appendTextLine);
        topHealingOnCarrierPlayers.forEach(topHealingOnCarrier::appendTextLine);

        //setting visibility to none
        holograms.forEach(hologram -> {
            hologram.getVisibilityManager().setVisibleByDefault(false);
        });

        this.holograms = holograms;
    }

    private void appendTeamDHP(Hologram hologram, Map<ChatColor, Long> map) {
        map.entrySet().stream().sorted(Map.Entry.<ChatColor, Long>comparingByValue().reversed()).forEach(chatColorLongEntry -> {
            ChatColor key = chatColorLongEntry.getKey();
            Long value = chatColorLongEntry.getValue();
            hologram.appendTextLine(key + (key == ChatColor.BLUE ? "Blue: " : "Red: ") + ChatColor.YELLOW + Utils.addCommaAndRound(value));
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
                gameHolograms.forEach(hologram -> hologram.getVisibilityManager().showTo(player));
            } else {
                gameHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            }
        }

        createGameSwitcherHologram(player);
    }

    private static void createGameSwitcherHologram(Player player) {
        HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                .filter(h -> h.getVisibilityManager().isVisibleTo(player) && h.getLocation().equals(DatabaseGame.gameSwitchLocation))
                .forEach(Hologram::delete);

        Hologram gameSwitcher = HologramsAPI.createHologram(Warlords.getInstance(), DatabaseGame.gameSwitchLocation);
        gameSwitcher.appendTextLine(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + "Last " + previousGames.size() + " Games");
        gameSwitcher.appendTextLine("");

        int selectedGame = LeaderboardManager.playerGameHolograms.get(player.getUniqueId());
        int gameBefore = getGameBefore(selectedGame);
        int gameAfter = getGameAfter(selectedGame);

        TextLine beforeLine;
        TextLine afterLine;
        if (gameBefore == previousGames.size() - 1) {
            beforeLine = gameSwitcher.appendTextLine(ChatColor.GRAY + "Latest Game");
        } else {
            beforeLine = gameSwitcher.appendTextLine(ChatColor.GRAY.toString() + (gameBefore + 1) + ". " + previousGames.get(gameBefore).getDate());
        }
        if (selectedGame == previousGames.size() - 1) {
            gameSwitcher.appendTextLine(ChatColor.GREEN + "Latest Game");
        } else {
            gameSwitcher.appendTextLine(ChatColor.GREEN.toString() + (selectedGame + 1) + ". " + previousGames.get(selectedGame).getDate());
        }

        if (gameAfter == previousGames.size() - 1) {
            afterLine = gameSwitcher.appendTextLine(ChatColor.GRAY + "Latest Game");
        } else {
            afterLine = gameSwitcher.appendTextLine(ChatColor.GRAY.toString() + (gameAfter + 1) + ". " + previousGames.get(gameAfter).getDate());
        }

        beforeLine.setTouchHandler((clicker) -> {
            LeaderboardManager.playerGameHolograms.put(player.getUniqueId(), gameBefore);
            setGameHologramVisibility(player);
        });

        afterLine.setTouchHandler((clicker) -> {
            LeaderboardManager.playerGameHolograms.put(player.getUniqueId(), gameAfter);
            setGameHologramVisibility(player);
        });

        gameSwitcher.getVisibilityManager().setVisibleByDefault(false);
        gameSwitcher.getVisibilityManager().showTo(player);
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

    public static void addGame(PlayingState gameState, boolean updatePlayerStats) {
        try {
            previousGames.get(0).deleteHolograms();
            previousGames.remove(0);
            DatabaseGame databaseGame = new DatabaseGame(gameState, updatePlayerStats);
            previousGames.add(databaseGame);
            databaseGame.createHolograms();

            addGameToDatabase(databaseGame);

            //sending message if player information remained the same
            for (WarlordsPlayer value : PlayerFilter.playingGame(gameState.getGame())) {
                if (value.getEntity().isOp()) {
                    if (updatePlayerStats) {
                        value.sendMessage(ChatColor.GREEN + "This game was added to the database and player information was updated");
                    } else {
                        value.sendMessage(ChatColor.GREEN + "This game was added to the database but player information remained the same");
                    }
                }
            }
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
                updatePlayerStatsFromGame(databaseGame,true);
            }
            Warlords.newChain().async(() -> DatabaseManager.gameService.create(databaseGame)).execute();
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
        DatabaseManager.loadPlayer(UUID.fromString(gamePlayer.getUuid()), PlayersCollections.WEEKLY);
        DatabaseManager.loadPlayer(UUID.fromString(gamePlayer.getUuid()), PlayersCollections.DAILY);

        DatabasePlayer databasePlayerAllTime = DatabaseManager.playerService.findByUUID(UUID.fromString(gamePlayer.getUuid()));
        DatabasePlayer databasePlayerWeekly = DatabaseManager.playerService.findOne(Criteria.where("uuid").is(gamePlayer.getUuid()), PlayersCollections.WEEKLY);
        DatabasePlayer databasePlayerDaily = DatabaseManager.playerService.findOne(Criteria.where("uuid").is(gamePlayer.getUuid()), PlayersCollections.DAILY);

        updatePlayerStats(databaseGame, add, gamePlayer, databasePlayerAllTime, blue);
        updatePlayerStats(databaseGame, add, gamePlayer, databasePlayerWeekly, blue);
        updatePlayerStats(databaseGame, add, gamePlayer, databasePlayerDaily, blue);

        DatabaseManager.updatePlayerAsync(databasePlayerAllTime);
        DatabaseManager.updatePlayerAsync(databasePlayerWeekly, PlayersCollections.WEEKLY);
        DatabaseManager.updatePlayerAsync(databasePlayerDaily, PlayersCollections.DAILY);
    }

    private static void updatePlayerStats(DatabaseGame databaseGame, boolean add, DatabaseGamePlayers.GamePlayer gamePlayer, DatabasePlayer databasePlayer, boolean checkBlueWin) {
        boolean won = checkBlueWin ? databaseGame.bluePoints > databaseGame.redPoints : databaseGame.redPoints > databaseGame.bluePoints;
        databasePlayer.updateStats(gamePlayer, won, add);
        databasePlayer.getClass(Classes.getClassesGroup(gamePlayer.getSpec())).updateStats(gamePlayer, won, add);
        databasePlayer.getSpec(gamePlayer.getSpec()).updateStats(gamePlayer, won, add);
    }

    @Transient
    public static String lastWarlordsPlusString = "";

    public static String getWarlordsPlusEndGameStats(PlayingState gameState) {
        StringBuilder output = new StringBuilder("Winners:");
        int bluePoints = gameState.getStats(Team.BLUE).points();
        int redPoints = gameState.getStats(Team.RED).points();
        if (bluePoints > redPoints) {
            for (WarlordsPlayer player : PlayerFilter.playingGame(gameState.getGame()).matchingTeam(Team.BLUE)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getTotalKills()).append(":").append(player.getTotalDeaths()).append("],");
            }
            output.setLength(output.length() - 1);
            output.append("Losers:");
            for (WarlordsPlayer player : PlayerFilter.playingGame(gameState.getGame()).matchingTeam(Team.RED)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getTotalKills()).append(":").append(player.getTotalDeaths()).append("],");
            }
        } else if (redPoints > bluePoints) {
            for (WarlordsPlayer player : PlayerFilter.playingGame(gameState.getGame()).matchingTeam(Team.RED)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getTotalKills()).append(":").append(player.getTotalDeaths()).append("],");
            }
            output.setLength(output.length() - 1);
            output.append("Losers:");
            for (WarlordsPlayer player : PlayerFilter.playingGame(gameState.getGame()).matchingTeam(Team.BLUE)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getTotalKills()).append(":").append(player.getTotalDeaths()).append("],");
            }
        } else {
            output.setLength(0);
            for (WarlordsPlayer player : PlayerFilter.playingGame(gameState.getGame()).matchingTeam(Team.BLUE)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getTotalKills()).append(":").append(player.getTotalDeaths()).append("],");
            }
            for (WarlordsPlayer player : PlayerFilter.playingGame(gameState.getGame()).matchingTeam(Team.RED)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getTotalKills()).append(":").append(player.getTotalDeaths()).append("],");
            }
        }
        output.setLength(output.length() - 1);
        if (BotManager.numberOfMessagesSentLast30Sec > 15) {
            if (BotManager.numberOfMessagesSentLast30Sec < 20) {
                BotManager.getTextChannelByName("games-backlog").ifPresent(textChannel -> textChannel.sendMessage("SOMETHING BROKEN DETECTED <@239929120035700737> <@253971614998331393>").queue());
            }
        } else {
            BotManager.getTextChannelByName("games-backlog").ifPresent(textChannel -> textChannel.sendMessage(output.toString()).queue());
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
