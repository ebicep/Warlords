package com.ebicep.warlords.database.repositories.games.pojos.duel;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
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

@Document(collection = "Games_Information_Duel")
public class DatabaseGameDuel extends DatabaseGameBase<DatabaseGamePlayerDuel> {

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
            this.players.computeIfAbsent(warlordsPlayer.getTeam(), team -> new ArrayList<>()).add(new DatabaseGamePlayerDuel(warlordsPlayer, gameWinEvent, counted));
        });
    }


    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase<DatabaseGamePlayerDuel> databaseGame, int multiplier) {
        for (List<DatabaseGamePlayerDuel> value : players.values()) {
            for (DatabaseGamePlayerDuel gamePlayerDuel : value) {
                DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame, gamePlayerDuel, multiplier);
            }
        }
    }

    @Override
    public Set<DatabaseGamePlayerDuel> getBasePlayers() {
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
    public void appendLastGameStats(Hologram hologram) {
        hologram.getLines().appendText(ChatColor.GRAY + date);
        hologram.getLines()
                .appendText(ChatColor.GREEN + map.getMapName() + ChatColor.GRAY + "  -  " + ChatColor.GREEN + timeLeft / 60 + ":" + timeLeft % 60 + (timeLeft % 60 < 10 ? "0" : ""));
        hologram.getLines().appendText(ChatColor.YELLOW + "Winner: " + winner.teamColor + winner.name);
    }

    @Override
    public void addCustomHolograms(List<Hologram> holograms) {

    }

    @Override
    public String getGameLabel() {
        return "";
    }

    @Override
    public Team getTeam(DatabaseGamePlayerBase player) {
        return null;
    }

    @Override
    public List<Component> getExtraLore() {
        return Arrays.asList(
                Component.text("Time Left: ", NamedTextColor.GRAY)
                         .append(Component.text(StringUtils.formatTimeLeft(timeLeft), NamedTextColor.GREEN)),
                Component.text("Winner: ", NamedTextColor.GRAY)
                         .append(Component.text(winner.name, winner.teamColor))
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
