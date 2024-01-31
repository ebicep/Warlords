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

public class Arathi extends GameMap {

    public Arathi() {
        super(
                "Arathi",
                60,
                18,
                60 * SECOND,
                "Arathi",
                1,
                GameMode.INTERCEPTION
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(173.5, 67, 426.5), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(736.5, 67, 450.5).yaw(-180), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(599.5, 56.5, 465.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(449.5, 95.5, 600.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(298.5, 56.5, 430.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(425.5, 15.5, 272.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(455.5, 67.5, 423.5), 45, 30));

        InterceptionPointOption farm = new InterceptionPointOption("Farm", loc.addXYZ(585.5, 54, 479.5));
        InterceptionPointOption lumbermill = new InterceptionPointOption("Lumbermill", loc.addXYZ(449.5, 96, 587.5));
        InterceptionPointOption blacksmith = new InterceptionPointOption("Blacksmith", loc.addXYZ(441.5, 65, 447.5));
        InterceptionPointOption mines = new InterceptionPointOption("Mines", loc.addXYZ(439.5, 12, 290.5));
        InterceptionPointOption stables = new InterceptionPointOption("Stables", loc.addXYZ(320.5, 56, 454.5));
        options.add(farm);
        options.add(lumbermill);
        options.add(blacksmith);
        options.add(mines);
        options.add(stables);

        options.add(SpawnpointOption.forTeam(loc.addXYZ(173.5, 67, 426.5), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(736.5, 67, 450.5).yaw(-180), Team.RED));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(621.5, 56, 448.5, 0, 0), farm));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(452.5, 96, 625.5, -180, 0), lumbermill));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(463.5, 63, 387, 0, 0), blacksmith));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(425.5, 32, 258.5, -90, 0), mines));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(290.5, 56, 418.5, 0, 0), stables));

        options.add(new GateOption(loc.addXYZ(183, 67, 447), loc.addXYZ(183, 64, 443)));
        options.add(new GateOption(loc.addXYZ(727, 67, 437), loc.addXYZ(727, 64, 441)));

        return options;
    }


}
