package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.SpawnpointOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import org.bukkit.Location;

import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class TowerDefenseTest extends GameMap {

    public TowerDefenseTest() {
        super(
                "Tower Defense Test",
                32,
                12,
                60 * SECOND,
                "TD",
                3,
                GameMode.TOWER_DEFENSE
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);
        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 65, 0.5, 90, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 65, 0.5, 90, 0), Team.RED).asOption());
        options.add(SpawnpointOption.forTeam(loc.addXYZ(2.5, 65, 0.5, 90, 0), Team.BLUE));

        options.add(SpawnpointOption.forTeam(loc.addXYZ(2.5, 65, 0.5, 90, 0), Team.RED));

        List<Location> path = List.of(
                loc.addXYZ(-12.5, 65, 0.5, 180, 0),
                loc.addXYZ(-12.5, 65, -19.5)
        );
        options.add(new TowerDefenseOption(new TowerDefenseOption.TowerDefensePath(path)));

        return options;
    }
}
