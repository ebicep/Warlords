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
import org.bukkit.Material;

import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class Neolithic extends GameMap {

    public Neolithic() {
        super(
                "Neolithic",
                60,
                18,
                60 * SECOND,
                "Neolithic",
                1,
                GameMode.INTERCEPTION
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-208.5, 62, -57.5, -90, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(216.5, 67, 58.5, 90, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(58.5, 62.5, 63.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(14.5, 54.5, -11.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(72.5, 46.5, -90.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-59.5, 55.5, -88.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-114.5, 81.5, 115.5), 45, 30));

        InterceptionPointOption shrine = new InterceptionPointOption("Shrine", loc.addXYZ(76.5, 65, 88.5));
        InterceptionPointOption tomb = new InterceptionPointOption("Tomb", loc.addXYZ(99.5, 44, -98.5));
        InterceptionPointOption chasm = new InterceptionPointOption("Chasm", loc.addXYZ(0.5, 54, 0.5));
        InterceptionPointOption leo = new InterceptionPointOption("Leo", loc.addXYZ(-114.5, 79, 98.5));
        InterceptionPointOption ruins = new InterceptionPointOption("Ruins", loc.addXYZ(-77.5, 58, -83.5));
        options.add(shrine);
        options.add(tomb);
        options.add(chasm);
        options.add(leo);
        options.add(ruins);

        options.add(SpawnpointOption.forTeam(loc.addXYZ(-208.5, 62, -57.5, -90, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(216.5, 67, 58.5, 90, 0), Team.RED));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(82.5, 63, 107.5, -180, 0), shrine));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(132.5, 46, -94.5, 91, 0), tomb));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-23.5, 56, 27.5, -180, 0), chasm));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-138.5, 78, 89.5, -90, 0), leo));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-107.5, 62, -81.5, -90, 0), ruins));


        options.add(new GateOption(loc.addXYZ(-175.5, 63, -50.5), loc.addXYZ(-175.5, 67, -64.5), Material.IRON_BARS));
        options.add(new GateOption(loc.addXYZ(178.5, 67, 65.5), loc.addXYZ(178.5, 73, 51.5), Material.IRON_BARS));

        return options;
    }

}