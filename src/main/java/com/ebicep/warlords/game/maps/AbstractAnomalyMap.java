package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.BasicScoreboardOption;
import com.ebicep.warlords.game.option.GraveOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.SpawnpointOption;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.pve.anomaly.AnomalyObjectiveMarker;
import com.ebicep.warlords.game.option.pve.anomaly.AnomalyOption;
import com.ebicep.warlords.game.option.pve.anomaly.AnomalySpawnMarker;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import org.bukkit.Location;

import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public abstract class AbstractAnomalyMap extends GameMap {

    private final double[] playerSpawn;
    private final double[][] objectiveLocations;
    private final double[][][] enemySpawnLocations;

    protected AbstractAnomalyMap(
            String mapName,
            String fileName,
            double[] playerSpawn,
            double[][] objectiveLocations,
            double[][][] enemySpawnLocations
    ) {
        super(mapName, 4, 1, 30 * SECOND, fileName, 3, GameMode.ANOMALY);
        this.playerSpawn = playerSpawn;
        this.objectiveLocations = objectiveLocations;
        this.enemySpawnLocations = enemySpawnLocations;
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);
        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());

        Location lobby = location(loc, playerSpawn);
        options.add(LobbyLocationMarker.create(lobby, Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(lobby, Team.RED).asOption());
        options.add(SpawnpointOption.forTeam(lobby, Team.BLUE));
        options.add(SpawnpointOption.forTeam(lobby, Team.RED));

        for (int objectiveIndex = 0; objectiveIndex < objectiveLocations.length; objectiveIndex++) {
            options.add(AnomalyObjectiveMarker.create(objectiveIndex, location(loc, objectiveLocations[objectiveIndex])).asOption());
            for (double[] spawn : enemySpawnLocations[objectiveIndex]) {
                options.add(AnomalySpawnMarker.create(objectiveIndex, location(loc, spawn)).asOption());
            }
        }

        options.add(new BoundingBoxOption(new Location(loc.getWorld(), -512, 0, -512), new Location(loc.getWorld(), 512, 255, 512)));
        options.add(new GraveOption());
        options.add(new BasicScoreboardOption());
        options.add(new AnomalyOption());
        return options;
    }

    private static Location location(LocationFactory loc, double[] coordinates) {
        return loc.addXYZ(coordinates[0], coordinates[1], coordinates[2]);
    }
}