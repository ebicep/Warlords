package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.option.marker.DebugLocationMarker;
import com.ebicep.warlords.maps.option.marker.SpawnLocationMarker;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.bukkit.Location;
import org.bukkit.Material;

public class SpawnpointOption implements Option {
    
    public static final int BAD_TEAM_PENALTY = -10000;
    
    @Nonnull
    private Game game;
    private final Location location;
    private final ToDoubleBiFunction<Team, Game> teamCheck;
    private final List<String> debugExtra;

    public SpawnpointOption(Location location, ToDoubleBiFunction<Team, Game> teamCheck, List<String> debugExtra) {
        this.location = location;
        this.teamCheck = teamCheck;
        this.debugExtra = debugExtra;
    }

    @Override
    public void register(Game game) {
        this.game = game;
        new SpawnLocationMarker() {
            @Override
            public double getPriority(Team team) {
                return teamCheck.applyAsDouble(team, game);
            }

            @Override
            public Location getLocation() {
                return location;
            }

        }.register(game);
        DebugLocationMarker.create(Material.BED, 0, SpawnpointOption.class, "Spawnpoint", location, () -> debugExtra).register(game);
    }
    
    public static SpawnpointOption forTeam(Location location, Team team) {
        return new SpawnpointOption(location, (t, g) -> t == team ? 0 : BAD_TEAM_PENALTY, Arrays.asList("Type: for-team", "Team: " + team));
    }
    
    /**
     * A spawnpoint that looks at the player distribution thoughout the game, and sets its own priority to place the player closest to friendlies and far away from enemies
     * @param location
     * @return 
     */
    public static SpawnpointOption avoidingEnemyPlayers(Location location) {
        return new SpawnpointOption(location, (t, g) -> {
            Location cache = new Location(null, 0, 0, 0);
            Map<Boolean, Double> distances = g
                    .offlinePlayers()
                    .map(w -> Warlords.getPlayer(w.getKey()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(
                            w -> t == null ? true : w.getTeam().equals(t),
                            Collectors.averagingDouble(
                                    w -> Math.pow(w.getLocation(cache).distanceSquared(location), 0.25)
                            )
                    ));
            double distanceToEnemy = distances.getOrDefault(Boolean.FALSE, 0d);
            double distanceToFriendlies = distances.getOrDefault(Boolean.TRUE, 0d);
            return distanceToFriendlies - distanceToEnemy;
        }, Arrays.asList("Type: avoiding-enemy-players"));
    }
}
