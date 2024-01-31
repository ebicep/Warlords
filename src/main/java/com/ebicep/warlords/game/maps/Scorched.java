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

public class Scorched extends GameMap {

    public Scorched() {
        super(
                "Scorched",
                60,
                18,
                60 * SECOND,
                "Scorched",
                1,
                GameMode.INTERCEPTION
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 43, -141.5, 0, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 43, 144.5, 180, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(113.5, 55.5, 5.5), 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-112.5, 55.5, 5.5), 45, 30));

        InterceptionPointOption towers = new InterceptionPointOption("Towers", loc.addXYZ(0.5, 44, 60.5));
        InterceptionPointOption gate = new InterceptionPointOption("Gate", loc.addXYZ(89.5, 54, 0.55));
        InterceptionPointOption crater = new InterceptionPointOption("Crater", loc.addXYZ(0.5, 28, 0.5));
        InterceptionPointOption aviary = new InterceptionPointOption("Aviary", loc.addXYZ(-88.5, 54, 0.5));
        InterceptionPointOption throne = new InterceptionPointOption("Throne", loc.addXYZ(0.5, 44, -59.5));
        options.add(towers);
        options.add(gate);
        options.add(crater);
        options.add(aviary);
        options.add(throne);

        options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 43, -141.5, 0, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 43, 144.5, 180, 0), Team.RED));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-0.5, 33, 72.5, 0, 0), towers));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(101.5, 54, 25.5, 135, 0), gate));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(0.5, 31, 32.5, -180, 0), crater));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-113.5, 54, -10.5, -90, 0), aviary));
        options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(0.5, 33, -73.5, -180, 0), throne));

        options.add(new GateOption(loc.addXYZ(-3.5, 43, -116.57), loc.addXYZ(4.5, 47, -116.5), Material.IRON_BARS));
        options.add(new GateOption(loc.addXYZ(-4.5, 43, 117.5), loc.addXYZ(5.5, 47, 117.5), Material.IRON_BARS));

        return options;
    }

}