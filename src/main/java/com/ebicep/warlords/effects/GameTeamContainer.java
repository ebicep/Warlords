package com.ebicep.warlords.effects;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import org.bukkit.entity.Player;

import java.util.stream.Stream;



// Class to quickly get a team's teammates and enemies

public class GameTeamContainer {

    private final Game game;
    private final Team team;

    public GameTeamContainer(Game game, Team team) {
        this.game = game;
        this.team = team;
    }

    public Stream<Player> getAllyPlayers() {
        return getAllyPlayers(game, team);
    }

    public static Stream<Player> getAllyPlayers(Game game, Team team) {
        return game.onlinePlayers().filter(e -> e.getValue() == team).map(e -> e.getKey());
    }

    public Stream<Player> getEnemyPlayers() {
        return getEnemyPlayers(game, team);
    }

    public static Stream<Player> getEnemyPlayers(Game game, Team team) {
        return game.onlinePlayers().filter(e -> e.getValue() != team).map(e -> e.getKey());
    }

}
