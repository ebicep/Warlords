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

public class DorivenBasin extends GameMap {

    public DorivenBasin() {
        super(
                "Doriven Basin",
                60,
                18,
                60 * SECOND,
                "DorivenBasin",
                1,
                GameMode.INTERCEPTION
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-24.5, 64, -110.5, 0, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(25.5, 64, 111.5, -180, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(116.5, 55.5, -12.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-116.5, 53.5, -11.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(87.5, 84.5, -101.5), PowerupOption.PowerUp.HEALING, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-136.5, 84.5, 93.5), PowerupOption.PowerUp.HEALING, 45, 30));

        InterceptionPointOption watchTower = new InterceptionPointOption("Watch Tower", loc.addXYZ(-97.5, 53, -9.5));
        InterceptionPointOption workshop = new InterceptionPointOption("Workshop", loc.addXYZ(-107.5, 83, 94.5));
        InterceptionPointOption ruins = new InterceptionPointOption("Ruins", loc.addXYZ(0.5, 86, 0.5));
        InterceptionPointOption house = new InterceptionPointOption("House", loc.addXYZ(108.5, 83, -93.5));
        InterceptionPointOption lumbermill = new InterceptionPointOption("Lumbermill", loc.addXYZ(98.5, 51, 10.5));
        options.add(watchTower);
        options.add(workshop);
        options.add(ruins);
        options.add(house);
        options.add(lumbermill);

        options.add(SpawnpointOption.forTeam(loc.addXYZ(-24.5, 64, -110.5, 0, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(25.5, 64, 111.5, -180, 0), Team.RED));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-111, 52, 13.5, -90, 0), watchTower));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-81.5, 83, 119.5, 90, 0), workshop));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(0.5, 83, -32.5, 0, 0), ruins));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(86.5, 83, -121.5, -90, 0), house));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(94.5, 54, 32.5, 180, 0), lumbermill));

        options.add(new GateOption(loc.addXYZ(-18.5, 71, -96.5), loc.addXYZ(-30.5, 64, -96.5)));
        options.add(new GateOption(loc.addXYZ(18.5, 69, 99.5), loc.addXYZ(32.5, 64, 99.5)));

        return options;
    }

}