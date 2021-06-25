package com.ebicep.warlords.effects;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import org.bukkit.entity.Player;

import java.util.Collection;


// Class to quickly get a team's teammates and enemies

public class GameTeamContainer {

    private final Game game;
    private final Team team;

    public GameTeamContainer(Game game, Team team) {
        this.game = game;
        this.team = team;
    }

    public Collection<Player> getAllyPlayers() {
        return getAllyPlayers(game, team);
    }

    public static Collection<Player> getAllyPlayers(Game game, Team team) {
        return team == Team.RED ? game.getTeamRedProtected() : game.getTeamBlueProtected();
    }

    public Collection<Player> getEnemyPlayers() {
        return getEnemyPlayers(game, team);
    }

    public static Collection<Player> getEnemyPlayers(Game game, Team team) {
        return team == Team.BLUE ? game.getTeamRedProtected() : game.getTeamBlueProtected();
    }

}
