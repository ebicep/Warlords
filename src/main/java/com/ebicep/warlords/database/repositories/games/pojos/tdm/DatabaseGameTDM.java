package com.ebicep.warlords.database.repositories.games.pojos.tdm;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGamePlayersCTF;
import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.WinAfterTimeoutOption;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Date;

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
        this.timeLeft = WinAfterTimeoutOption.getTimeLeft(game).orElse(-1);
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
