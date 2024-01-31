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

public class SunAndMoon extends GameMap {

    public SunAndMoon() {
        super(
                "Sun and Moon",
                60,
                18,
                60 * SECOND,
                "SunAndMoon",
                1,
                GameMode.INTERCEPTION
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(115.5, 89.5, 0.5, 90, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-114.5, 89.5, 0.5, -90, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(0.5, 76.5, 31.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(47.5, 65.5, 78.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-7.5, 73.5, 105.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-50.5, 67.5, -84.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(8.5, 73.5, -104.5), 45, 30));

        InterceptionPointOption eclipse = new InterceptionPointOption("Eclipse", loc.addXYZ(0.5, 85, 0.5));
        InterceptionPointOption glacier = new InterceptionPointOption("Glacier", loc.addXYZ(23.5, 62, 70.5));
        InterceptionPointOption sun = new InterceptionPointOption("Sun", loc.addXYZ(-18.5, 67, 134.5));
        InterceptionPointOption moon = new InterceptionPointOption("Moon", loc.addXYZ(19.5, 67, -133.5));
        InterceptionPointOption valley = new InterceptionPointOption("Valley", loc.addXYZ(-22.5, 62, -69.5));
        options.add(eclipse);
        options.add(glacier);
        options.add(sun);
        options.add(moon);
        options.add(valley);

        options.add(SpawnpointOption.forTeam(loc.addXYZ(115.5, 89.5, 0.5, 90, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-114.5, 89.5, 0.5, -90, 0), Team.RED));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(0.5, 77, -14.5, 180, 0), eclipse));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(52.5, 66, 62.5, 90, 0), glacier));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-23.5, 70, 170.5, 180, 0), sun));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(25.5, 70, -167.5, 0, 0), moon));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-51.5, 66, -60.5, -90, 0), valley));

        options.add(new GateOption(loc.addXYZ(-100, 89, 4), loc.addXYZ(-100, 93, -3)));
        options.add(new GateOption(loc.addXYZ(100, 89, 4), loc.addXYZ(100, 93, -3)));

        return options;
    }

}