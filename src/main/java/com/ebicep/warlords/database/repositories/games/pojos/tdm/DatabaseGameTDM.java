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
import java.util.Arrays;
import java.util.List;

@Document(collection = "Games_Information_TDM")
public class DatabaseGameTDM extends DatabaseGameBase {

    @Field("time_left")
    protected int timeLeft;
    protected Team winner;
    @Field("blue_points")
    protected int bluePoints;
    @Field("red_points")
    protected int redPoints;
    protected DatabaseGamePlayersTDM players;

    public DatabaseGameTDM() {

    }

    public DatabaseGameTDM(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, counted);
        this.timeLeft = WinAfterTimeoutOption.getTimeRemaining(game).orElse(-1);
        this.winner = gameWinEvent == null || gameWinEvent.isCancelled() ? null : gameWinEvent.getDeclaredWinner();
        this.bluePoints = game.getPoints(Team.BLUE);
        this.redPoints = game.getPoints(Team.RED);
        this.players = new DatabaseGamePlayersTDM(game);
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
    public void updatePlayerStatsFromGame(DatabaseGameBase databaseGame, boolean add) {
        players.blue.forEach(gamePlayerTDM -> DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame, gamePlayerTDM, add));
        players.red.forEach(gamePlayerTDM -> DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame, gamePlayerTDM, add));
    }

    @Override
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        assert player instanceof DatabaseGamePlayersTDM.DatabaseGamePlayerTDM;

        if (bluePoints > redPoints) {
            return players.blue.contains((DatabaseGamePlayersTDM.DatabaseGamePlayerTDM) player) ? DatabaseGamePlayerResult.WON : DatabaseGamePlayerResult.LOST;
        } else if (redPoints > bluePoints) {
            return players.red.contains((DatabaseGamePlayersTDM.DatabaseGamePlayerTDM) player) ? DatabaseGamePlayerResult.WON : DatabaseGamePlayerResult.LOST;
        } else {
            return DatabaseGamePlayerResult.DRAW;
        }
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
                ChatColor.GRAY + "Players: " + ChatColor.YELLOW + (players.getBlue().size() + players.getRed().size())
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

    public DatabaseGamePlayersTDM getPlayers() {
        return players;
    }

    public void setPlayers(DatabaseGamePlayersTDM players) {
        this.players = players;
    }
}
