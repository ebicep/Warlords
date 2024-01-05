package com.ebicep.warlords.database.repositories.games.pojos.siege;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pvp.siege.SiegeOption;
import com.ebicep.warlords.util.java.StringUtils;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@Document(collection = "Games_Information_Siege")
public class DatabaseGameSiege extends DatabaseGameBase {

    @Field("time_elapsed")
    protected int timeElapsed; //seconds
    protected Team winner;
    @Field("blue_points")
    protected int bluePoints;
    @Field("red_points")
    protected int redPoints;
    protected Map<Team, List<DatabaseGamePlayerSiege>> players = new LinkedHashMap<>();


    public DatabaseGameSiege() {
    }

    public DatabaseGameSiege(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, counted);
        for (Option option : game.getOptions()) {
            if (option instanceof SiegeOption siegeOption) {
                this.timeElapsed = siegeOption.getTotalTicksElapsed() / 20;
                break;
            }
        }
        this.winner = gameWinEvent == null || gameWinEvent.isCancelled() ? null : gameWinEvent.getDeclaredWinner();
        this.bluePoints = game.getPoints(Team.BLUE);
        this.redPoints = game.getPoints(Team.RED);
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            this.players.computeIfAbsent(warlordsPlayer.getTeam(), team -> new ArrayList<>()).add(new DatabaseGamePlayerSiege(warlordsPlayer, gameWinEvent, counted));
        });
    }

    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase databaseGame, int multiplier) {
        for (List<DatabaseGamePlayerSiege> gamePlayerCTFList : players.values()) {
            for (DatabaseGamePlayerSiege gamePlayerCTF : gamePlayerCTFList) {
                DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame, gamePlayerCTF, multiplier);
            }
        }
    }

    @Override
    public Set<? extends DatabaseGamePlayerBase> getBasePlayers() {
        return players.values().stream()
                      .flatMap(Collection::stream)
                      .collect(Collectors.toSet());
    }

    @Override
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        assert player instanceof DatabaseGamePlayerSiege;

        if (winner == null) {
            return DatabaseGamePlayerResult.DRAW;
        }
        for (Map.Entry<Team, List<DatabaseGamePlayerSiege>> teamListEntry : players.entrySet()) {
            if (teamListEntry.getValue().contains(player)) {
                return teamListEntry.getKey() == winner ? DatabaseGamePlayerResult.WON : DatabaseGamePlayerResult.LOST;
            }
        }
        return DatabaseGamePlayerResult.NONE;
    }

    @Override
    public void appendLastGameStats(Hologram hologram) {
        hologram.getLines().appendText(ChatColor.GRAY + date);
        hologram.getLines()
                .appendText(ChatColor.GREEN + map.getMapName() + ChatColor.GRAY + "  -  " + ChatColor.GREEN + timeElapsed / 60 + ":" + timeElapsed % 60 + (timeElapsed % 60 < 10 ? "0" : ""));
        hologram.getLines().appendText(ChatColor.BLUE.toString() + bluePoints + ChatColor.GRAY + "  -  " + ChatColor.RED + redPoints);
    }

    @Override
    public void addCustomHolograms(List<Hologram> holograms) {
//        Hologram topDHPPerMinute = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.TOP_DHP_PER_MINUTE_LOCATION);
//        holograms.add(topDHPPerMinute);
//        topDHPPerMinute.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top DHP per Minute");
//
//        List<String> topDHPPerGamePlayers = new ArrayList<>();
//
//        int minutes = (15 - (int) Math.round(timeLeft / 60.0)) == 0 ? 1 : 15 - (int) Math.round(timeLeft / 60.0);
//        List<DatabaseGamePlayerCTF> allPlayers = players
//                .values()
//                .stream()
//                .flatMap(Collection::stream)
//                .toList();
//        HashMap<DatabaseGamePlayerCTF, ChatColor> playerColor = new HashMap<>();
//        for (Map.Entry<Team, List<DatabaseGamePlayerCTF>> teamListEntry : players.entrySet()) {
//            for (DatabaseGamePlayerCTF gamePlayerCTF : teamListEntry.getValue()) {
//                playerColor.put(gamePlayerCTF, teamListEntry.getKey().oldTeamColor);
//            }
//        }
//
//        allPlayers.stream()
//                  .sorted((o1, o2) -> {
//                      Long p1DHPPerGame = o1.getTotalDHP() / minutes;
//                      Long p2DHPPerGame = o2.getTotalDHP() / minutes;
//                      return p2DHPPerGame.compareTo(p1DHPPerGame);
//                  }).forEach(databaseGamePlayer -> {
//                      topDHPPerGamePlayers.add(playerColor.get(databaseGamePlayer) + databaseGamePlayer.getName() + ": " +
//                              ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalDHP() / minutes));
//                  });
//
//        allPlayers.stream()
//                  .sorted(Comparator.comparingLong(DatabaseGamePlayerCTF::getTotalDamageOnCarrier).reversed())
//                  .forEach(databaseGamePlayer -> {
//                      topDamageOnCarrierPlayers.add(playerColor.get(databaseGamePlayer) + databaseGamePlayer.getName() + ": " +
//                              ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalDamageOnCarrier()));
//                  });
//
//        allPlayers.stream()
//                  .sorted(Comparator.comparingLong(DatabaseGamePlayerCTF::getTotalHealingOnCarrier).reversed())
//                  .forEach(databaseGamePlayer -> {
//                      topHealingOnCarrierPlayers.add(playerColor.get(databaseGamePlayer) + databaseGamePlayer.getName() + ": " +
//                              ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalHealingOnCarrier()));
//                  });
//
//        topDHPPerGamePlayers.forEach(s -> topDHPPerMinute.getLines().appendText(s));
    }


    @Override
    public String getGameLabel() {
        return "";
    }

    @Override
    public Team getTeam(DatabaseGamePlayerBase player) {
        return players.entrySet()
                      .stream()
                      .filter(teamListEntry -> teamListEntry.getValue()
                                                            .stream()
                                                            .anyMatch(databaseGamePlayerSiege -> databaseGamePlayerSiege.getUuid().equals(player.getUuid())))
                      .map(Map.Entry::getKey)
                      .findFirst()
                      .orElse(null);
    }

    @Override
    public List<Component> getExtraLore() {
        return Arrays.asList(
                Component.text("Time Left: ", NamedTextColor.GRAY)
                         .append(Component.text(StringUtils.formatTimeLeft(timeElapsed), NamedTextColor.GREEN)),
                Component.text("Winner: ", NamedTextColor.GRAY)
                         .append(Component.text(winner.name, winner.teamColor)),
                Component.text("Blue Points: ", NamedTextColor.GRAY)
                         .append(Component.text(bluePoints, NamedTextColor.BLUE)),
                Component.text("Red Points: ", NamedTextColor.GRAY)
                         .append(Component.text(redPoints, NamedTextColor.RED)),
                Component.text("Players: ", NamedTextColor.GRAY)
                         .append(Component.text(players.values().stream().mapToLong(Collection::size).sum(), NamedTextColor.YELLOW))
        );
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(int timeElapsed) {
        this.timeElapsed = timeElapsed;
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
}
