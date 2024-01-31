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
import com.ebicep.warlords.game.option.pvp.siege.SiegeOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import org.bukkit.Material;

import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class GorgeRemasteredSiege extends GameMap {

    public GorgeRemasteredSiege() {
        super(
                "Gorge Remastered",
                12,
                8,
                60 * SECOND,
                "GorgeRemastered2",
                1,
                GameMode.SIEGE
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);
        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());

        options.add(LobbyLocationMarker.create(loc.addXYZ(43.5, 76, -216.5, 90, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-134.5, 76, -216.5, -90, 0), Team.RED).asOption());

        options.add(SpawnpointOption.forTeam(loc.addXYZ(53.5, 82, -216.5, 90, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-144.5, 82, -216.5, -90, 0), Team.RED));

        options.add(new GateOption(loc, -125.5, 81.5, -213.5, -125.5, 76, -219.5, Material.SPRUCE_FENCE));
        options.add(new GateOption(loc, 34.5, 80, -219.5, 34.5, 76, -213.5, Material.IRON_BARS));
        options.add(new GateOption(loc, -145.5, 76, -212.5, -143.5, 80, -212.5, Material.SPRUCE_FENCE));
        options.add(new GateOption(loc, 54.5, 76, -220.5, 52.5, 80, -220.5, Material.IRON_BARS));
        options.add(new GateOption(loc, -132.5, 76, -232.5, -132.5, 81, -234.5, Material.SPRUCE_FENCE));
        options.add(new GateOption(loc, 41.5, 76, -200.5, 41.5, 81, -198.5, Material.IRON_BARS));

//            options.add(SpawnpointOption.forTeam(loc.addXYZ(5.5, 71, -159.5, 135, 0), Team.BLUE));
//            options.add(SpawnpointOption.forTeam(loc.addXYZ(-97.5, 71, -274.5, -45, 0), Team.RED));

        options.add(new GateOption(loc, -125.5, 82.5, -213.5, -125.5, 76, -219.5, Material.SPRUCE_FENCE));
        options.add(new GateOption(loc, 34.5, 81, -219.5, 34.5, 76, -213.5, Material.IRON_BARS));

        options.add(new PowerupOption(loc.addXYZ(-2.5, 61.5, -236.5), PowerupOption.PowerUp.ENERGY));
        options.add(new PowerupOption(loc.addXYZ(-88.5, 61.5, -196.5), PowerupOption.PowerUp.ENERGY));

        options.add(new PowerupOption(loc.addXYZ(-152.5, 75.5, -208.5), PowerupOption.PowerUp.SPEED));
        options.add(new PowerupOption(loc.addXYZ(60.5, 75.5, -224.5), PowerupOption.PowerUp.SPEED));
        options.add(new PowerupOption(loc.addXYZ(-152.5, 76.5, -232.5), PowerupOption.PowerUp.SPEED));
        options.add(new PowerupOption(loc.addXYZ(62.5, 76, -200.5), PowerupOption.PowerUp.SPEED));

        options.add(new PowerupOption(loc.addXYZ(-12.5, 45.5, -194.5), PowerupOption.PowerUp.HEALING));
        options.add(new PowerupOption(loc.addXYZ(-78.5, 45.5, -238.5), PowerupOption.PowerUp.HEALING));

        options.add(new PowerupOption(loc.addXYZ(-32, 66.5, -165), PowerupOption.PowerUp.PAYLOAD_BATTERY, 45, 120));
        options.add(new PowerupOption(loc.addXYZ(-59, 66.5, -268), PowerupOption.PowerUp.PAYLOAD_BATTERY, 45, 120));

        options.add(new SiegeOption(loc.addXYZ(-45.5, 44, -216.5))
                .addPayloadStart(Team.BLUE, loc.addXYZ(-45.5, 44, -216.5, 0, 0))
                .addPayloadStart(Team.RED, loc.addXYZ(-45.5, 44, -216.5, 180, 0)));

        return options;
    }

}