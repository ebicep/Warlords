package com.ebicep.warlords.database.repositories.games.pojos.ctf;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.WinAfterTimeoutOption;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.NumberFormat;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import me.filoghost.holographicdisplays.api.beta.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.beta.hologram.Hologram;
import me.filoghost.holographicdisplays.api.beta.hologram.VisibilitySettings;
import org.bukkit.ChatColor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@Document(collection = "Games_Information")
public class DatabaseGameCTF extends DatabaseGameBase {

    @Field("time_left")
    protected int timeLeft;
    protected Team winner;
    @Field("blue_points")
    protected int bluePoints;
    @Field("red_points")
    protected int redPoints;
    protected DatabaseGamePlayersCTF players;
    @Field("stat_info")
    protected String statInfo;

    public DatabaseGameCTF() {
    }

    public DatabaseGameCTF(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        this.timeLeft = WinAfterTimeoutOption.getTimeLeft(game).orElse(-1);
        this.winner = gameWinEvent == null || gameWinEvent.isCancelled() ? null : gameWinEvent.getDeclaredWinner();
        this.bluePoints = game.getPoints(Team.BLUE);
        this.redPoints = game.getPoints(Team.RED);
        this.players = new DatabaseGamePlayersCTF(game);
        this.statInfo = getWarlordsPlusEndGameStats(game);

        this.exactDate = new Date();
        this.date = DATE_FORMAT.format(new Date());
        this.map = game.getMap();
        this.gameMode = GameMode.CAPTURE_THE_FLAG;
        this.gameAddons = Arrays.asList(game.getAddons().toArray(new GameAddon[0]));
        this.counted = counted;
    }

    @Override
    public String toString() {
        return "DatabaseGameCTF{" +
                "id='" + id + '\'' +
                ", exactDate=" + exactDate +
                ", date='" + date + '\'' +
                ", map=" + map +
                ", gameMode=" + gameMode +
                ", gameAddons=" + gameAddons +
                ", counted=" + counted +
                ", timeLeft=" + timeLeft +
                ", winner=" + winner +
                ", bluePoints=" + bluePoints +
                ", redPoints=" + redPoints +
                ", players=" + players +
                ", statInfo='" + statInfo + '\'' +
                '}';
    }

    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase databaseGame, boolean add) {
        players.blue.forEach(gamePlayerCTF -> DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame, gamePlayerCTF, add));
        players.red.forEach(gamePlayerCTF -> DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame, gamePlayerCTF, add));
    }

    @Override
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        assert player instanceof DatabaseGamePlayersCTF.DatabaseGamePlayerCTF;

        if (bluePoints > redPoints) {
            return players.blue.contains((DatabaseGamePlayersCTF.DatabaseGamePlayerCTF) player) ? DatabaseGamePlayerResult.WON : DatabaseGamePlayerResult.LOST;
        } else if (redPoints > bluePoints) {
            return players.red.contains((DatabaseGamePlayersCTF.DatabaseGamePlayerCTF) player) ? DatabaseGamePlayerResult.WON : DatabaseGamePlayerResult.LOST;
        } else {
            return DatabaseGamePlayerResult.DRAW;
        }
    }


    @Override
    public void createHolograms() {
        List<Hologram> holograms = new ArrayList<>();

        //readding game holograms
        Hologram lastGameStats = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.lastGameStatsLocation);
        holograms.add(lastGameStats);
        lastGameStats.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Last " + (isPrivate() ? "Comp" : "Pub") + " Game Stats");

        Hologram topDamage = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.topDamageLocation);
        holograms.add(topDamage);
        topDamage.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Damage");

        Hologram topHealing = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.topHealingLocation);
        holograms.add(topHealing);
        topHealing.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Healing");

        Hologram topAbsorbed = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.topAbsorbedLocation);
        holograms.add(topAbsorbed);
        topAbsorbed.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Absorbed");

        Hologram topDHPPerMinute = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.topDHPPerMinuteLocation);
        holograms.add(topDHPPerMinute);
        topDHPPerMinute.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top DHP per Minute");

        Hologram topDamageOnCarrier = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.topDamageOnCarrierLocation);
        holograms.add(topDamageOnCarrier);
        topDamageOnCarrier.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Damage On Carrier");

        Hologram topHealingOnCarrier = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.topHealingOnCarrierLocation);
        holograms.add(topHealingOnCarrier);
        topHealingOnCarrier.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Healing On Carrier");

        //last game stats
        int minutes = (15 - (int) Math.round(timeLeft / 60.0)) == 0 ? 1 : 15 - (int) Math.round(timeLeft / 60.0);
        lastGameStats.getLines().appendText(ChatColor.GRAY + date);
        lastGameStats.getLines().appendText(ChatColor.GREEN + map.getMapName() + ChatColor.GRAY + "  -  " + ChatColor.GREEN + timeLeft / 60 + ":" + timeLeft % 60 + (timeLeft % 60 < 10 ? "0" : ""));
        lastGameStats.getLines().appendText(ChatColor.BLUE.toString() + bluePoints + ChatColor.GRAY + "  -  " + ChatColor.RED + redPoints);


        List<DatabaseGamePlayersCTF.DatabaseGamePlayerCTF> bluePlayers = players.blue;
        List<DatabaseGamePlayersCTF.DatabaseGamePlayerCTF> redPlayers = players.red;
        List<DatabaseGamePlayersCTF.DatabaseGamePlayerCTF> allPlayers = new ArrayList<>();
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

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayersCTF.DatabaseGamePlayerCTF::getTotalDamage).reversed()).forEach(databaseGamePlayer -> {
            totalDamage.put(bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED, totalDamage.getOrDefault(bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED, 0L) + databaseGamePlayer.getTotalDamage());
            topDamagePlayers.add((bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED) + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalDamage()));
        });

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayersCTF.DatabaseGamePlayerCTF::getTotalHealing).reversed()).forEach(databaseGamePlayer -> {
            totalHealing.put(bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED, totalHealing.getOrDefault(bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED, 0L) + databaseGamePlayer.getTotalHealing());
            topHealingPlayers.add((bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED) + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalHealing()));
        });

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayersCTF.DatabaseGamePlayerCTF::getTotalAbsorbed).reversed()).forEach(databaseGamePlayer -> {
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

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayersCTF.DatabaseGamePlayerCTF::getTotalDamageOnCarrier).reversed()).forEach(databaseGamePlayer -> {
            topDamageOnCarrierPlayers.add((bluePlayers.contains(databaseGamePlayer) ? ChatColor.BLUE : ChatColor.RED) + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalDamageOnCarrier()));
        });

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayersCTF.DatabaseGamePlayerCTF::getTotalHealingOnCarrier).reversed()).forEach(databaseGamePlayer -> {
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

    @Override
    public String getGameLabel() {
        return ChatColor.GRAY + date + ChatColor.DARK_GRAY + " - " +
                ChatColor.GREEN + map + ChatColor.DARK_GRAY + " - " +
                ChatColor.GRAY + "(" + ChatColor.BLUE + bluePoints + ChatColor.GRAY + ":" + ChatColor.RED + redPoints + ChatColor.GRAY + ")" + ChatColor.DARK_GRAY + " - " + ChatColor.DARK_PURPLE + isCounted();
    }

    private void appendTeamDHP(Hologram hologram, Map<ChatColor, Long> map) {
        map.entrySet().stream().sorted(Map.Entry.<ChatColor, Long>comparingByValue().reversed()).forEach(chatColorLongEntry -> {
            ChatColor key = chatColorLongEntry.getKey();
            Long value = chatColorLongEntry.getValue();
            hologram.getLines().appendText(key + (key == ChatColor.BLUE ? "Blue: " : "Red: ") + ChatColor.YELLOW + NumberFormat.addCommaAndRound(value));
        });
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

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public Team getWinner() {
        return winner;
    }

    public void setWinner(Team winner) {
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

    public DatabaseGamePlayersCTF getPlayers() {
        return players;
    }

    public void setPlayers(DatabaseGamePlayersCTF players) {
        this.players = players;
    }

    public String getStatInfo() {
        return statInfo;
    }

    public void setStatInfo(String statInfo) {
        this.statInfo = statInfo;
    }

    public static String getLastWarlordsPlusString() {
        return lastWarlordsPlusString;
    }
}
