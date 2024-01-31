package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.PowerupOption;
import com.ebicep.warlords.game.option.SpawnpointOption;
import com.ebicep.warlords.game.option.cuboid.GateOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.pvp.interception.InterceptionPointOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;

import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class DeathValley extends GameMap {

    public DeathValley() {
        super(
                "Death Valley",
                60,
                18,
                60 * SECOND,
                "DeathValley",
                1,
                GameMode.INTERCEPTION
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(117.5, 5, -122.5, 0, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-211.5, 7, -119.5, 0, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(-41.5, 19.5, 4.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(65.5, 7.5, 56.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-40.5, 4.5, 67.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-179.5, 14.5, 59.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-67.5, 4.5, -121.5), 45, 30));

        InterceptionPointOption bridge = new InterceptionPointOption("Bridge", loc.addXYZ(-46.5, 4, -114.5));
        InterceptionPointOption stables = new InterceptionPointOption("Stables", loc.addXYZ(85.5, 8, 57.5));
        InterceptionPointOption stump = new InterceptionPointOption("Stump", loc.addXYZ(-50.5, 13, -4.5));
        InterceptionPointOption butchers = new InterceptionPointOption("Butchers", loc.addXYZ(-199.5, 7, 37.5));
        InterceptionPointOption quarry = new InterceptionPointOption("Quarry", loc.addXYZ(-47.5, 3, 59.5));
        options.add(bridge);
        options.add(stables);
        options.add(stump);
        options.add(butchers);
        options.add(quarry);

        options.add(SpawnpointOption.forTeam(loc.addXYZ(117.5, 5, -122.5, 0, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-211.5, 7, -119.5, 0, 0), Team.RED));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-52.5, 5, -93.5, -180, 0), bridge));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(67.5, 7, 26.5, -30, 0), stables));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-59.5, 12, -34.5, 0, 0), stump));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-171.5, 7, 21, 60, 0), butchers));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-58.5, 4, 65.5, -118, 0), quarry));

        options.add(new GateOption(loc.addXYZ(101.5, 6, -95.5), loc.addXYZ(105.5, 13, -95.53)));
        options.add(new GateOption(loc.addXYZ(124.5, 6, -93.5), loc.addXYZ(129.5, 13, -93.5)));
        options.add(new GateOption(loc.addXYZ(-202.5, 7, -87.5), loc.addXYZ(-195.5, 15, -87.5)));
        options.add(new GateOption(loc.addXYZ(-225.5, 7, -88.5), loc.addXYZ(-218.5, 15, -88.5)));

        return options;
    }

}