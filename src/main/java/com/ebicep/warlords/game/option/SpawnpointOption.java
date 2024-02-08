package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.DebugLocationMarker;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.game.option.pvp.interception.InterceptionPointOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class SpawnpointOption extends MarkerOption {

    public static final int BAD_TEAM_PENALTY = -10000;

    public static SpawnpointOption forTeam(Location location, Team team) {
        return new SpawnpointOption(
                location,
                (p) -> p.getTeam() == team ? 0 : BAD_TEAM_PENALTY,
                t -> t == team ? 0 : BAD_TEAM_PENALTY,
                Arrays.asList(Component.text("Type: for-team"), Component.text("Team: " + team))
        );
    }

    public static InterceptionSpawnPoint interceptionPoint(Location location, InterceptionPointOption interceptionPointOption) {
        return new InterceptionSpawnPoint(
                location,
                (p) -> {
                    if (p.getTeam() == null) {
                        return -10;
                    }
                    boolean inConflict = interceptionPointOption.isInConflict();
                    if (inConflict) { // enemy + team on point
                        return 10;
                    }
                    Team teamOwning = interceptionPointOption.getTeamOwning();
                    Team teamInCircle = interceptionPointOption.getTeamInCircle();
                    if (p.getTeam() == teamOwning) {
                        if (teamInCircle != teamOwning) { // enemy capping
                            return 10;
                        }
                        return 5; // own point
                    }
                    return -10; // enemy point or uncaptured
                },
                Arrays.asList(Component.text("Type: interception-point"), Component.text("Interception point: " + interceptionPointOption)),
                interceptionPointOption
        );
    }

    /**
     * A spawnpoint that looks at the player distribution throughout the game,
     * and sets its own priority to place the player closest to friendlies and
     * far away from enemies
     *
     * @param location The location of this point
     * @return
     */
    public static SpawnpointOption avoidingEnemyPlayers(Location location) {
        return new SpawnpointOption(
                location,
                (p) -> {
                    Location cache = new Location(null, 0, 0, 0);
                    Map<Boolean, Double> distances = p
                            .getGame()
                            .offlinePlayersWithoutSpectators()
                            .map(w -> Warlords.getPlayer(w.getKey()))
                            .filter(o -> o != null && o.isAlive())
                            .collect(Collectors.groupingBy(
                                    w -> p.getTeam() == null || w.getTeam() == p.getTeam(),
                                    Collectors.averagingDouble(
                                            w -> Math.pow(w.getLocation(cache).distanceSquared(location), 0.25)
                                    )
                            ));
                    double distanceToEnemy = distances.getOrDefault(Boolean.FALSE, 0d);
                    double distanceToFriendlies = distances.getOrDefault(Boolean.TRUE, 0d);
                    return distanceToFriendlies - distanceToEnemy;
                },
                team -> 0,
                List.of(Component.text("Type: avoiding-enemy-players"))
        );
    }

    public SpawnpointOption(Location location, ToDoubleFunction<WarlordsEntity> playerTeamCheck, ToDoubleFunction<Team> teamCheck, List<TextComponent> debugExtra) {
        super(new SpawnLocationMarker() {
                  @Override
                  public double getPriority(WarlordsEntity player) {
                      if (player == null) {
                          return 0;
                      }
                      return playerTeamCheck.applyAsDouble(player);
                  }

                  @Override
                  public double getPriorityTeam(Team team) {
                      return teamCheck.applyAsDouble(team);
                  }

                  @Override
                  public Location getLocation() {
                      return location;
                  }

              },
                DebugLocationMarker.create(Material.BLACK_BED, 0, SpawnpointOption.class, Component.text("Spawnpoint"), location, () -> debugExtra)
        );
    }

    public static class InterceptionSpawnPoint extends SpawnpointOption {

        private final Location location;
        private final InterceptionPointOption interceptionPoint;

        public InterceptionSpawnPoint(Location location, ToDoubleFunction<WarlordsEntity> teamCheck, List<TextComponent> debugExtra, InterceptionPointOption interceptionPoint) {
            super(location, teamCheck, t -> 0, debugExtra);
            this.location = location;
            this.interceptionPoint = interceptionPoint;
        }

        public Location getLocation() {
            return location;
        }

        public InterceptionPointOption getInterceptionPoint() {
            return interceptionPoint;
        }
    }

}
