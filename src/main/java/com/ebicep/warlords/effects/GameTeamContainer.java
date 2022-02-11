package com.ebicep.warlords.effects;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import org.bukkit.entity.Player;

import java.util.stream.Stream;
import javax.annotation.Nullable;



// Class to quickly get a team's teammates and enemies

public class GameTeamContainer {

    private final Game game;
    private final Team team;

    public GameTeamContainer(Game game, @Nullable Team team) {
        this.game = game;
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public Stream<Player> getAllyPlayers() {
        return getAllyPlayers(game, team);
    }

    public static Stream<Player> getAllyPlayers(Game game, Team team) {
        return game.onlinePlayersWithoutSpectators().filter(e -> e.getValue() == team).map(e -> e.getKey());
    }

    public Stream<Player> getEnemyPlayers() {
        return getEnemyPlayers(game, team);
    }

    public static Stream<Player> getEnemyPlayers(Game game, Team team) {
        return game.onlinePlayersWithoutSpectators().filter(e -> e.getValue() != team).map(e -> e.getKey());
    }

}
