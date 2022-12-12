package com.ebicep.warlords.database.repositories.games.pojos.duel;

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
import java.util.stream.Collectors;

@Document(collection = "Games_Information_Duel")
public class DatabaseGameDuel extends DatabaseGameBase {

    @Field("time_left")
    protected int timeLeft;
    protected Team winner;
    protected Map<Team, List<DatabaseGamePlayerDuel>> players = new LinkedHashMap<>();

    public DatabaseGameDuel() {
    }

    public DatabaseGameDuel(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, counted);
        this.timeLeft = WinAfterTimeoutOption.getTimeRemaining(game).orElse(-1);
        this.winner = gameWinEvent == null || gameWinEvent.isCancelled() ? null : gameWinEvent.getDeclaredWinner();
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            this.players.computeIfAbsent(warlordsPlayer.getTeam(), team -> new ArrayList<>()).add(new DatabaseGamePlayerDuel(warlordsPlayer));
        });
    }


    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase databaseGame, int multiplier) {
        for (List<DatabaseGamePlayerDuel> value : players.values()) {
            for (DatabaseGamePlayerDuel gamePlayerDuel : value) {
                DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame, gamePlayerDuel, multiplier);
            }
        }
    }

    @Override
    public Set<DatabaseGamePlayerBase> getBasePlayers() {
        return players.values().stream()
                      .flatMap(Collection::stream)
                      .collect(Collectors.toSet());
    }

    @Override
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        assert player instanceof DatabaseGamePlayerDuel;

        if (winner == null) {
            return DatabaseGamePlayerResult.DRAW;
        }
        for (Map.Entry<Team, List<DatabaseGamePlayerDuel>> teamListEntry : players.entrySet()) {
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
        return "";
    }

    @Override
    public List<String> getExtraLore() {
        return Arrays.asList(
                ChatColor.GRAY + "Time Left: " + ChatColor.GREEN + Utils.formatTimeLeft(timeLeft),
                ChatColor.GRAY + "Winner: " + winner.teamColor + winner.name
        );
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public Team getWinner() {
        return winner;
    }

    public Map<Team, List<DatabaseGamePlayerDuel>> getPlayers() {
        return players;
    }
}
