package com.ebicep.warlords.database.repositories.games.pojos.duel;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.WinAfterTimeoutOption;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Date;

@Document(collection = "Games_Information_Duel")
public class DatabaseGameDuel extends DatabaseGameBase {

    @Field("time_left")
    protected int timeLeft;
    protected Team winner;
    protected DatabaseGamePlayersDuel players;

    public DatabaseGameDuel() {
    }

    public DatabaseGameDuel(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, counted);
        this.timeLeft = WinAfterTimeoutOption.getTimeLeft(game).orElse(-1);
        this.winner = gameWinEvent == null || gameWinEvent.isCancelled() ? null : gameWinEvent.getDeclaredWinner();
        this.players = new DatabaseGamePlayersDuel(game);
    }


    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase databaseGame, boolean add) {
        players.blue.forEach(gamePlayerDuel -> DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame, gamePlayerDuel, add));
        players.red.forEach(gamePlayerDuel -> DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame, gamePlayerDuel, add));
    }

    @Override
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        assert player instanceof DatabaseGamePlayersDuel.DatabaseGamePlayerDuel;

        if (winner == Team.BLUE) {
            return players.blue.contains((DatabaseGamePlayersDuel.DatabaseGamePlayerDuel) player) ? DatabaseGamePlayerResult.WON : DatabaseGamePlayerResult.LOST;
        } else if (winner == Team.RED) {
            return players.red.contains((DatabaseGamePlayersDuel.DatabaseGamePlayerDuel) player) ? DatabaseGamePlayerResult.WON : DatabaseGamePlayerResult.LOST;
        } else {
            return DatabaseGamePlayerResult.DRAW;
        }
    }

    @Override
    public void createHolograms() {

    }

    @Override
    public String getGameLabel() {
        return "";
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public Team getWinner() {
        return winner;
    }

    public DatabaseGamePlayersDuel getPlayers() {
        return players;
    }
}
