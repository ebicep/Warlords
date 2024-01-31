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

public class Phantom extends GameMap {

    public Phantom() {
        super(
                "Phantom",
                60,
                18,
                60 * SECOND,
                "Phantom",
                1,
                GameMode.INTERCEPTION
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(174.5, 74, -0.5, 90, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-170.5, 72, 2.5, -90, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(-60.5, 78.5, -94.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-31.5, 66.5, 84.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(18.5, 76.5, 0.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(33.5, 66.5, -84.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(61.5, 78.5, 95.5), 45, 30));

        InterceptionPointOption apollo = new InterceptionPointOption("Apollo", loc.addXYZ(62.5, 79, 81.5));
        InterceptionPointOption pond = new InterceptionPointOption("Pond", loc.addXYZ(48.5, 66, -67.5));
        InterceptionPointOption altar = new InterceptionPointOption("Altar", loc.addXYZ(0.5, 73, 0.5));
        InterceptionPointOption inferno = new InterceptionPointOption("Inferno", loc.addXYZ(-47.5, 65, 68.5));
        InterceptionPointOption monument = new InterceptionPointOption("Monument", loc.addXYZ(-61.5, 78, -80.5));
        options.add(apollo);
        options.add(pond);
        options.add(altar);
        options.add(inferno);
        options.add(monument);

        options.add(SpawnpointOption.forTeam(loc.addXYZ(174.5, 74, -0.5, 90, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-170.5, 72, 2.5, -90, 0), Team.RED));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(62.5, 74, 32.5, 0, 0), apollo));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(71.5, 66, -86.5, 45, 0), pond));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-20.5, 76, -1.5, -90, 0), altar));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-70.5, 66, 85.5, -125, 0), inferno));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-61.5, 74, -31.5, -180, 0), monument));

        options.add(new GateOption(loc.addXYZ(155.5, 74, 2.5), loc.addXYZ(155.5, 78, -3.5)));
        options.add(new GateOption(loc.addXYZ(-156.5, 72, 8.5), loc.addXYZ(-156.5, 76, -3.5)));

        return options;
    }

}