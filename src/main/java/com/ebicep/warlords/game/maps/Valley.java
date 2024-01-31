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

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class Valley extends GameMap {

    public Valley() {
        super(
                "Atherrough Valley",
                32,
                12,
                60 * SECOND,
                "Atherrough_Valley",
                3,
                GameMode.CAPTURE_THE_FLAG
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-22.5, 39, -83.5, -180, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(23.5, 39, 83.5), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(5.5, 15.5, -33.5), PowerupOption.PowerUp.ENERGY));
        options.add(new PowerupOption(loc.addXYZ(-4.5, 15.5, 34.5), PowerupOption.PowerUp.ENERGY));

        options.add(new PowerupOption(loc.addXYZ(4.5, 25.5, -86.5), PowerupOption.PowerUp.SPEED));
        options.add(new PowerupOption(loc.addXYZ(-3.5, 25.5, 87.5), PowerupOption.PowerUp.SPEED));

        options.add(new PowerupOption(loc.addXYZ(57.5, 15.5, 1.5), PowerupOption.PowerUp.HEALING));
        options.add(new PowerupOption(loc.addXYZ(-56.5, 15.5, -0.5), PowerupOption.PowerUp.HEALING));

        options.add(SpawnpointOption.forTeam(loc.addXYZ(39.5, 28.5, -97.5), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-38.5, 28.5, 97.5, -180, 0), Team.RED));

        options.add(new FlagCapturePointOption(loc.addXYZ(-29.5, 38.5, -88.5, -90, 0), Team.BLUE));
        options.add(new FlagSpawnPointOption(loc.addXYZ(-29.5, 38.5, -88.5, -90, 0), Team.BLUE));

        options.add(new FlagCapturePointOption(loc.addXYZ(30.5, 38.5, 89.5, 90, 0), Team.RED));
        options.add(new FlagSpawnPointOption(loc.addXYZ(30.5, 38.5, 89.5, 90, 0), Team.RED));

        options.add(new GateOption(loc.addXYZ(-26, 33, -96), loc.addXYZ(-19, 40, -96)));
        options.add(new GateOption(loc.addXYZ(-28, 31, -81), loc.addXYZ(-28, 41, -75)));
        options.add(new GateOption(loc.addXYZ(20, 33, 96), loc.addXYZ(26, 42, 96)));
        options.add(new GateOption(loc.addXYZ(29, 31, 76), loc.addXYZ(29, 41, 82)));

        options.add(new WinByPointsOption());
        options.add(new MercyWinOption());
        if (addons.contains(GameAddon.DOUBLE_TIME)) {
            options.add(new WinAfterTimeoutOption(1800));
        } else {
            options.add(new WinAfterTimeoutOption());
        }
        options.add(new GameOvertimeOption());
        options.add(new AbstractScoreOnEventOption.FlagCapture());
        options.add(new AbstractScoreOnEventOption.OnKill());
        options.add(new RespawnWaveOption());
        options.add(new RespawnProtectionOption());
        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

        return options;
    }


}
