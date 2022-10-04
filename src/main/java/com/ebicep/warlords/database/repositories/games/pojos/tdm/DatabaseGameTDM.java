package com.ebicep.warlords.database.repositories.games.pojos.tdm;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.WinAfterTimeoutOption;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@Document(collection = "Games_Information_TDM")
public class DatabaseGameTDM extends DatabaseGameBase {

    @Field("time_left")
    protected int timeLeft;
    protected Team winner;
    @Field("blue_points")
    protected int bluePoints;
    @Field("red_points")
    protected int redPoints;
    protected Map<Team, List<DatabaseGamePlayerTDM>> players = new LinkedHashMap<>();


    public DatabaseGameTDM() {

    }

    public DatabaseGameTDM(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, counted);
        this.timeLeft = WinAfterTimeoutOption.getTimeRemaining(game).orElse(-1);
        this.winner = gameWinEvent == null || gameWinEvent.isCancelled() ? null : gameWinEvent.getDeclaredWinner();
        this.bluePoints = game.getPoints(Team.BLUE);
        this.redPoints = game.getPoints(Team.RED);
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            this.players.computeIfAbsent(warlordsPlayer.getTeam(), team -> new ArrayList<>()).add(new DatabaseGamePlayerTDM(warlordsPlayer));
        });
    }

    @Override
    public String toString() {
        return "DatabaseGameTDM{" +
                "id='" + id + '\'' +
                ", exactDate=" + exactDate +
                ", date='" + date + '\'' +
                ", map=" + map +
                ", gameMode=" + gameMode +
                ", gameAddons=" + gameAddons +
                ", counted=" + counted +
                ", bluePoints=" + bluePoints +
                ", redPoints=" + redPoints +
                '}';
    }

    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase databaseGame, int multiplier) {
        for (List<DatabaseGamePlayerTDM> gamePlayerCTFList : players.values()) {
            for (DatabaseGamePlayerTDM gamePlayerCTF : gamePlayerCTFList) {
                DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame, gamePlayerCTF, multiplier);
            }
        }
    }

    @Override
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        assert player instanceof DatabaseGamePlayerTDM;

        if (winner == null) {
            return DatabaseGamePlayerResult.DRAW;
        }
        for (Map.Entry<Team, List<DatabaseGamePlayerTDM>> teamListEntry : players.entrySet()) {
            if (teamListEntry.getValue().contains(player)) {
                return teamListEntry.getKey() == winner ? DatabaseGamePlayerResult.WON : DatabaseGamePlayerResult.LOST;
            }
        }
        return DatabaseGamePlayerResult.NONE;
    }

    @Override
    public void createHolograms() {

    }

    @Override
    public String getGameLabel() {
        return ChatColor.GRAY + date + ChatColor.DARK_GRAY + " - " +
                ChatColor.GREEN + map + ChatColor.DARK_GRAY + " - " +
                ChatColor.GRAY + "(" + ChatColor.BLUE + bluePoints + ChatColor.GRAY + ":" + ChatColor.RED + redPoints + ChatColor.GRAY + ")" + ChatColor.DARK_GRAY + " - " + ChatColor.DARK_PURPLE + isCounted();

    }

    @Override
    public List<String> getExtraLore() {
        return Arrays.asList(
                ChatColor.GRAY + "Time Left: " + ChatColor.GREEN + Utils.formatTimeLeft(timeLeft),
                ChatColor.GRAY + "Winner: " + winner.teamColor + winner.name,
                ChatColor.GRAY + "Blue Points: " + ChatColor.BLUE + bluePoints,
                ChatColor.GRAY + "Red Points: " + ChatColor.RED + redPoints,
                ChatColor.GRAY + "Players: " + ChatColor.YELLOW + players.values().stream().mapToLong(Collection::size).sum()
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

    public Map<Team, List<DatabaseGamePlayerTDM>> getPlayers() {
        return players;
    }
}
