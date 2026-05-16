package com.ebicep.warlords.database.repositories.games.pojos.ctf;

import com.ebicep.holograms.Hologram;
import com.ebicep.holograms.HologramDataText;
import com.ebicep.jda.BalanceThreadContext;
import com.ebicep.jda.BotManager;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.bson.types.ObjectId;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.game.option.win.WinByPointsOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.StringUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Display;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Document(collection = "Games_Information_CTF")
public class DatabaseGameCTF extends DatabaseGameBase<DatabaseGamePlayerCTF> {

    @Transient
    public static String lastWarlordsPlusString = "";

    public static String getWarlordsPlusEndGameStats(Game game) {
        StringBuilder output = new StringBuilder("Winners:");
        int bluePoints = game.getPoints(Team.BLUE);
        int redPoints = game.getPoints(Team.RED);
        if (bluePoints > redPoints) {
            for (WarlordsEntity player : PlayerFilter.playingGame(game).matchingTeam(Team.BLUE)) {
                output.append(player.getUuid().toString().replace("-", ""))
                      .append("[")
                      .append(player.getMinuteStats().total().getKills())
                      .append(":")
                      .append(player.getMinuteStats().total().getDeaths())
                      .append("],");
            }
            output.setLength(output.length() - 1);
            output.append("Losers:");
            for (WarlordsEntity player : PlayerFilter.playingGame(game).matchingTeam(Team.RED)) {
                output.append(player.getUuid().toString().replace("-", ""))
                      .append("[")
                      .append(player.getMinuteStats().total().getKills())
                      .append(":")
                      .append(player.getMinuteStats().total().getDeaths())
                      .append("],");
            }
        } else if (redPoints > bluePoints) {
            for (WarlordsEntity player : PlayerFilter.playingGame(game).matchingTeam(Team.RED)) {
                output.append(player.getUuid().toString().replace("-", ""))
                      .append("[")
                      .append(player.getMinuteStats().total().getKills())
                      .append(":")
                      .append(player.getMinuteStats().total().getDeaths())
                      .append("],");
            }
            output.setLength(output.length() - 1);
            output.append("Losers:");
            for (WarlordsEntity player : PlayerFilter.playingGame(game).matchingTeam(Team.BLUE)) {
                output.append(player.getUuid().toString().replace("-", ""))
                      .append("[")
                      .append(player.getMinuteStats().total().getKills())
                      .append(":")
                      .append(player.getMinuteStats().total().getDeaths())
                      .append("],");
            }
        } else {
            output.setLength(0);
            for (WarlordsEntity player : PlayerFilter.playingGame(game).matchingTeam(Team.BLUE)) {
                output.append(player.getUuid().toString().replace("-", ""))
                      .append("[")
                      .append(player.getMinuteStats().total().getKills())
                      .append(":")
                      .append(player.getMinuteStats().total().getDeaths())
                      .append("],");
            }
            for (WarlordsEntity player : PlayerFilter.playingGame(game).matchingTeam(Team.RED)) {
                output.append(player.getUuid().toString().replace("-", ""))
                      .append("[")
                      .append(player.getMinuteStats().total().getKills())
                      .append(":")
                      .append(player.getMinuteStats().total().getDeaths())
                      .append("],");
            }
        }
        output.setLength(output.length() - 1);
        lastWarlordsPlusString = output.toString();
        return output.toString();
    }

    public static void sendGamesBacklogJson(DatabaseGameCTF databaseGame) {
        BotManager.DiscordServer comps = BotManager.getServer("comps");
        if (comps == null) {
            return;
        }
        if (BotManager.numberOfMessagesSentLast30Sec > 15) {
            if (BotManager.numberOfMessagesSentLast30Sec < 20) {
                comps.getChannel(BotManager.BotChannel.GAMES_BACKLOG)
                     .ifPresent(textChannel -> textChannel.sendMessage("SOMETHING BROKEN DETECTED <@239929120035700737> <@253971614998331393>").queue());
            }
            return;
        }
        if (databaseGame.getId() == null) {
            databaseGame.setId(new ObjectId().toHexString());
        }
        String json = buildGamesBacklogJson(databaseGame);
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        String fileName = databaseGame.getId() + ".json";
        comps.getChannel(BotManager.BotChannel.GAMES_BACKLOG)
             .ifPresent(textChannel -> textChannel.sendFiles(FileUpload.fromData(jsonBytes, fileName)).queue());
        sendGamesBacklogJsonToBalanceThread(jsonBytes, fileName);
    }

    public static void sendGamesBacklogJsonToLatestBalanceThread(DatabaseGameCTF databaseGame) {
        if (databaseGame.getId() == null) {
            databaseGame.setId(new ObjectId().toHexString());
        }
        String json = buildGamesBacklogJson(databaseGame);
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        sendGamesBacklogJsonToBalanceThread(jsonBytes, databaseGame.getId() + ".json");
    }

    private static void sendGamesBacklogJsonToBalanceThread(byte[] jsonBytes, String fileName) {
        long threadId = BalanceThreadContext.getLatestBalanceThreadId();
        if (threadId == 0 || BotManager.jda == null) {
            return;
        }
        ThreadChannel threadChannel = BotManager.jda.getThreadChannelById(threadId);
        if (threadChannel == null) {
            ChatUtils.MessageType.DISCORD_BOT.sendMessage("Balance thread " + threadId + " not found for games-backlog JSON");
            BalanceThreadContext.clearActiveBalanceThreadId();
            return;
        }
        threadChannel.sendFiles(FileUpload.fromData(jsonBytes, fileName)).queue(
                success -> BalanceThreadContext.clearActiveBalanceThreadId(),
                failure -> {
                    ChatUtils.MessageType.DISCORD_BOT.sendErrorMessage(failure);
                    BalanceThreadContext.clearActiveBalanceThreadId();
                }
        );
    }

    public static String buildGamesBacklogJson(DatabaseGameCTF databaseGame) {
        Team winnerTeam;
        Team loserTeam;
        if (databaseGame.getWinner() != null) {
            winnerTeam = databaseGame.getWinner();
            loserTeam = winnerTeam == Team.BLUE ? Team.RED : Team.BLUE;
        } else if (databaseGame.getBluePoints() > databaseGame.getRedPoints()) {
            winnerTeam = Team.BLUE;
            loserTeam = Team.RED;
        } else if (databaseGame.getRedPoints() > databaseGame.getBluePoints()) {
            winnerTeam = Team.RED;
            loserTeam = Team.BLUE;
        } else {
            winnerTeam = Team.BLUE;
            loserTeam = Team.RED;
        }
        List<BacklogPlayer> winners = toBacklogPlayers(databaseGame.getPlayers().getOrDefault(winnerTeam, List.of()));
        List<BacklogPlayer> losers = toBacklogPlayers(databaseGame.getPlayers().getOrDefault(loserTeam, List.of()));
        BacklogPayload payload = new BacklogPayload(winners, losers, databaseGame.getId());
        return new GsonBuilder().setPrettyPrinting().create().toJson(payload);
    }

    private static List<BacklogPlayer> toBacklogPlayers(List<DatabaseGamePlayerCTF> players) {
        return players.stream()
                    .map(player -> new BacklogPlayer(
                            player.getUuid().toString(),
                            player.getName(),
                            player.getTotalKills(),
                            player.getTotalDeaths()
                    ))
                    .toList();
    }

    public static String getLastWarlordsPlusString() {
        return lastWarlordsPlusString;
    }

    private record BacklogPlayer(String uuid, String name, int kills, int deaths) {
    }

    private record BacklogPayload(List<BacklogPlayer> winners, List<BacklogPlayer> losers, @SerializedName("game_id") String gameId) {
    }

    @Field("time_left")
    protected int timeLeft;
    @Field("time_initial")
    protected int timeInitial;
    protected Team winner;
    @Field("target_points")
    protected int targetPoints;
    @Field("blue_points")
    protected int bluePoints;
    @Field("red_points")
    protected int redPoints;
    protected Map<Team, List<DatabaseGamePlayerCTF>> players = new LinkedHashMap<>();

    public DatabaseGameCTF() {
    }

    public DatabaseGameCTF(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, counted);
        this.timeLeft = WinAfterTimeoutOption.getTimeRemaining(game).orElse(-1);
        this.timeInitial = WinAfterTimeoutOption.getTimeInitial(game).orElse(-1);
        this.winner = gameWinEvent == null || gameWinEvent.isCancelled() ? null : gameWinEvent.getDeclaredWinner();
        this.targetPoints = WinByPointsOption.getPointLimit(game).orElse(-1);
        this.bluePoints = game.getPoints(Team.BLUE);
        this.redPoints = game.getPoints(Team.RED);
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            this.players.computeIfAbsent(warlordsPlayer.getTeam(), team -> new ArrayList<>()).add(new DatabaseGamePlayerCTF(warlordsPlayer, gameWinEvent, counted));
        });
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
                '}';
    }

    @Override
    public void appendLastGameStats(ComponentBuilder componentBuilder) {
        componentBuilder.newLine(ChatColor.GRAY + date);
        componentBuilder.newLine(ChatColor.GREEN + map.getMapName() + ChatColor.GRAY + "  -  " + ChatColor.GREEN + timeLeft / 60 + ":" + timeLeft % 60 + (timeLeft % 60 < 10 ? "0" : ""));
        componentBuilder.newLine(ChatColor.BLUE.toString() + bluePoints + ChatColor.GRAY + "  -  " + ChatColor.RED + redPoints);
    }

    @Override
    public Set<DatabaseGamePlayerCTF> getBasePlayers() {
        return players.values().stream()
                      .flatMap(Collection::stream)
                      .collect(Collectors.toSet());
    }

    @Override
    public Team getTeam(DatabaseGamePlayerBase player) {
        return players.entrySet()
                      .stream()
                      .filter(teamListEntry -> teamListEntry.getValue()
                                                            .stream()
                                                            .anyMatch(databaseGamePlayerCTF -> databaseGamePlayerCTF.getUuid().equals(player.getUuid())))
                      .map(Map.Entry::getKey)
                      .findFirst()
                      .orElse(null);
    }

    @Override
    public void addCustomHolograms(List<Hologram> holograms) {
        ComponentBuilder topDHPPerMinuteComponent = ComponentBuilder.create("Top DHP per Minute", NamedTextColor.AQUA, TextDecoration.BOLD);
        ComponentBuilder topDamageOnCarrierComponent = ComponentBuilder.create("Top Damage On Carrier", NamedTextColor.AQUA, TextDecoration.BOLD);
        ComponentBuilder topHealingOnCarrierComponent = ComponentBuilder.create("Top Healing On Carrier", NamedTextColor.AQUA, TextDecoration.BOLD);

        List<String> topDHPPerGamePlayers = new ArrayList<>();
        List<String> topDamageOnCarrierPlayers = new ArrayList<>();
        List<String> topHealingOnCarrierPlayers = new ArrayList<>();

        int minutes = (15 - (int) Math.round(timeLeft / 60.0)) == 0 ? 1 : 15 - (int) Math.round(timeLeft / 60.0);
        List<DatabaseGamePlayerCTF> allPlayers = players
                .values()
                .stream()
                .flatMap(Collection::stream)
                .toList();
        HashMap<DatabaseGamePlayerCTF, ChatColor> playerColor = new HashMap<>();
        for (Map.Entry<Team, List<DatabaseGamePlayerCTF>> teamListEntry : players.entrySet()) {
            for (DatabaseGamePlayerCTF gamePlayerCTF : teamListEntry.getValue()) {
                playerColor.put(gamePlayerCTF, teamListEntry.getKey().getChatColor());
            }
        }

        allPlayers.stream()
                  .sorted((o1, o2) -> {
                      Long p1DHPPerGame = o1.getTotalDHP() / minutes;
                      Long p2DHPPerGame = o2.getTotalDHP() / minutes;
                      return p2DHPPerGame.compareTo(p1DHPPerGame);
                  }).forEach(databaseGamePlayer -> {
                      topDHPPerGamePlayers.add("  " + playerColor.get(databaseGamePlayer) + databaseGamePlayer.getName() + ": " +
                              ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalDHP() / minutes) + "  ");
                  });

        allPlayers.stream()
                  .sorted(Comparator.comparingLong(DatabaseGamePlayerCTF::getTotalDamageOnCarrier).reversed())
                  .forEach(databaseGamePlayer -> {
                      topDamageOnCarrierPlayers.add("  " + playerColor.get(databaseGamePlayer) + databaseGamePlayer.getName() + ": " +
                              ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalDamageOnCarrier()) + "  ");
                  });

        allPlayers.stream()
                  .sorted(Comparator.comparingLong(DatabaseGamePlayerCTF::getTotalHealingOnCarrier).reversed())
                  .forEach(databaseGamePlayer -> {
                      topHealingOnCarrierPlayers.add("  " + playerColor.get(databaseGamePlayer) + databaseGamePlayer.getName() + ": " +
                              ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalHealingOnCarrier()) + "  ");
                  });

        topDHPPerGamePlayers.forEach(topDHPPerMinuteComponent::newLine);
        topDamageOnCarrierPlayers.forEach(topDamageOnCarrierComponent::newLine);
        topHealingOnCarrierPlayers.forEach(topHealingOnCarrierComponent::newLine);

        HologramDataText topDHPPerMinuteData = new HologramDataText.Builder<>(topDHPPerMinuteComponent.build())
                .setBillboard(Display.Billboard.FIXED)
                .build();
        Hologram topDHPPerMinute = new Hologram.Builder("topDHPPerMinute" + exactDate,
                TOP_DHP_PER_MINUTE_LOCATION,
                p -> topDHPPerMinuteData
        ).build();
        holograms.add(topDHPPerMinute);

        HologramDataText topDamageOnCarrierData = new HologramDataText.Builder<>(topDamageOnCarrierComponent.build())
                .setBillboard(Display.Billboard.FIXED)
                .build();
        Hologram topDamageOnCarrier = new Hologram.Builder("topDamageOnCarrier" + exactDate,
                TOP_DAMAGE_ON_CARRIER_LOCATION,
                p -> topDamageOnCarrierData
        ).build();
        holograms.add(topDamageOnCarrier);

        HologramDataText topHealingOnCarrierData = new HologramDataText.Builder<>(topHealingOnCarrierComponent.build())
                .setBillboard(Display.Billboard.FIXED)
                .build();
        Hologram topHealingOnCarrier = new Hologram.Builder("topHealingOnCarrier" + exactDate,
                TOP_HEALING_ON_CARRIER_LOCATION,
                p -> topHealingOnCarrierData
        ).build();
        holograms.add(topHealingOnCarrier);
    }

    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase<DatabaseGamePlayerCTF> databaseGame, int multiplier) {
        for (List<DatabaseGamePlayerCTF> gamePlayerCTFList : players.values()) {
            for (DatabaseGamePlayerCTF gamePlayerCTF : gamePlayerCTFList) {
                DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame, gamePlayerCTF, multiplier);
            }
        }
    }

    @Override
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        assert player instanceof DatabaseGamePlayerCTF;

        if (winner == null) {
            return DatabaseGamePlayerResult.DRAW;
        }
        for (Map.Entry<Team, List<DatabaseGamePlayerCTF>> teamListEntry : players.entrySet()) {
            if (teamListEntry.getValue().contains(player)) {
                return teamListEntry.getKey() == winner ? DatabaseGamePlayerResult.WON : DatabaseGamePlayerResult.LOST;
            }
        }
        return DatabaseGamePlayerResult.NONE;
    }

    @Override
    public String getGameLabel() {
        return ChatColor.GRAY + date + ChatColor.DARK_GRAY + " - " +
                ChatColor.GREEN + map + ChatColor.DARK_GRAY + " - " +
                ChatColor.GRAY + "(" + ChatColor.BLUE + bluePoints + ChatColor.GRAY + ":" + ChatColor.RED + redPoints + ChatColor.GRAY + ")" + ChatColor.DARK_GRAY + " - " + ChatColor.DARK_PURPLE + isCounted();
    }

    @Override
    public List<Component> getExtraLore() {
        return Arrays.asList(
                Component.text("Time Left: ", NamedTextColor.GRAY)
                         .append(Component.text(StringUtils.formatTimeLeft(timeLeft), NamedTextColor.GREEN)),
                Component.text("Winner: ", NamedTextColor.GRAY)
                         .append(Component.text(winner.name, winner.getTeamColor())),
                Component.text("Blue Points: ", NamedTextColor.GRAY)
                         .append(Component.text(bluePoints, NamedTextColor.BLUE)),
                Component.text("Red Points: ", NamedTextColor.GRAY)
                         .append(Component.text(redPoints, NamedTextColor.RED)),
                Component.text("Players: ", NamedTextColor.GRAY)
                         .append(Component.text(players.values().stream().mapToLong(Collection::size).sum(), NamedTextColor.YELLOW))

        );
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

    public Map<Team, List<DatabaseGamePlayerCTF>> getPlayers() {
        return players;
    }
}
