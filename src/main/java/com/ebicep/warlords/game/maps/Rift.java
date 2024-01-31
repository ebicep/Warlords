package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.cuboid.AbstractCuboidOption;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.cuboid.GateOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.pvp.FlagCapturePointOption;
import com.ebicep.warlords.game.option.pvp.FlagSpawnPointOption;
import com.ebicep.warlords.game.option.pvp.GameOvertimeOption;
import com.ebicep.warlords.game.option.respawn.RespawnProtectionOption;
import com.ebicep.warlords.game.option.respawn.RespawnWaveOption;
import com.ebicep.warlords.game.option.win.MercyWinOption;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.game.option.win.WinByPointsOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;

import java.util.EnumSet;
import java.util.List;

public class Rift extends GameMap {

    public Rift() {
        super(
                "The Rift",
                32,
                12,
                60 * SECOND,
                "Rift",
                3,
                GameMode.CAPTURE_THE_FLAG
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);
        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-86.5, 46, -33.5), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(87.5, 46, 35.5, 180, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(-32.5, 25.5, 49.5), PowerupOption.PowerUp.ENERGY));
        options.add(new PowerupOption(loc.addXYZ(33.5, 25.5, -48.5), PowerupOption.PowerUp.ENERGY));

        options.add(new PowerupOption(loc.addXYZ(-54.5, 36.5, 24.5), PowerupOption.PowerUp.SPEED));
        options.add(new PowerupOption(loc.addXYZ(55.5, 36.5, -23.5), PowerupOption.PowerUp.SPEED));

        options.add(new PowerupOption(loc.addXYZ(-0.5, 24.5, 64.5), PowerupOption.PowerUp.HEALING));
        options.add(new PowerupOption(loc.addXYZ(1.5, 24.5, -62.5), PowerupOption.PowerUp.HEALING));

        options.add(SpawnpointOption.forTeam(loc.addXYZ(-32.5, 34.5, -43.5, -90, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(33, 34.5, 45, 90, 0), Team.RED));

        options.add(new FlagCapturePointOption(loc.addXYZ(-98.5, 45.5, -17.5, -90, 0), Team.BLUE));
        options.add(new FlagSpawnPointOption(loc.addXYZ(-98.5, 45.5, -17.5, -90, 0), Team.BLUE));

        options.add(new FlagCapturePointOption(loc.addXYZ(99.5, 45.5, 17.5, 90, 0), Team.RED));
        options.add(new FlagSpawnPointOption(loc.addXYZ(99.5, 45.5, 17.5, 90, 0), Team.RED));

        options.add(new AbstractScoreOnEventOption.FlagCapture(250));

        options.add(new GateOption(loc, -79, 45, -29, -79, 49, -24));
        options.add(new GateOption(loc, -91, 45, -6, -86, 49, -6));
        options.add(new GateOption(loc, 79, 45, 25, 79, 49, 29));
        options.add(new GateOption(loc, 87, 45, 6, 91, 49, 6));

        options.add(new WinByPointsOption());
        options.add(new MercyWinOption());
        if (addons.contains(GameAddon.DOUBLE_TIME)) {
            options.add(new WinAfterTimeoutOption(1800));
        } else {
            options.add(new WinAfterTimeoutOption());
        }
        options.add(new GameOvertimeOption());
        options.add(new AbstractScoreOnEventOption.OnKill(5));
        options.add(new RespawnWaveOption());
        options.add(new RespawnProtectionOption());
        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

        return options;
    }

}
