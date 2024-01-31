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

import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class Payload2 extends GameMap {

    public Payload2() {
        super(
                "Payload2",
                32,
                12,
                5 * SECOND,
                "Payload2",
                3,
                GameMode.SIEGE
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);
        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());

        options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 1, 18, 180, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 1, -32.5, 0, 0), Team.RED).asOption());

        options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 1, 18, 180, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 1, -32.5, 0, 0), Team.RED));

        options.add(new PowerupOption(loc.addXYZ(10, 1.5, 0), PowerupOption.PowerUp.PAYLOAD_BATTERY, 45, 10)); //120

        options.add(new SiegeOption(loc.addXYZ(0.5, 1, -7.5))
                .addPayloadStart(Team.BLUE, loc.addXYZ(0.5, 1, -7.5, 180, 0))
                .addPayloadStart(Team.RED, loc.addXYZ(0.5, 1, -7.5, 0, 0))
        );

        options.add(new GateOption(loc, -5, 1, 15, 5, 10, 15));
        options.add(new GateOption(loc, -5, 1, -30, 5, 10, -30));

        return options;
    }
}