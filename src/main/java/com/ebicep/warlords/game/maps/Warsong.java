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

public class Warsong extends GameMap {

    public Warsong() {
        super(
                "Warsong Remastered",
                32,
                12,
                60 * SECOND,
                "Warsong",
                2,
                GameMode.CAPTURE_THE_FLAG
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(71.5, 40, -71.5, 90, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(73.5, 41, 213.5, -90, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(102.5, 21.5, 51.5), PowerupOption.PowerUp.ENERGY));
        options.add(new PowerupOption(loc.addXYZ(42.5, 21.5, 92.5), PowerupOption.PowerUp.ENERGY));

        options.add(new PowerupOption(loc.addXYZ(63.5, 33.5, -31.5), PowerupOption.PowerUp.SPEED));
        options.add(new PowerupOption(loc.addXYZ(79.5, 32.5, 167.5), PowerupOption.PowerUp.SPEED));

        options.add(new PowerupOption(loc.addXYZ(44.5, 20.5, 42.5), PowerupOption.PowerUp.HEALING));
        options.add(new PowerupOption(loc.addXYZ(100.5, 20.5, 101.5), PowerupOption.PowerUp.HEALING));

        options.add(SpawnpointOption.forTeam(loc.addXYZ(45.5, 29.5, 15.5, -30, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(99.5, 29.5, 128.5, 150, 0), Team.RED));

        options.add(new FlagCapturePointOption(loc.addXYZ(56.5, 39.5, -76.5), Team.BLUE));
        options.add(new FlagSpawnPointOption(loc.addXYZ(56.5, 39.5, -76.5), Team.BLUE));

        options.add(new FlagCapturePointOption(loc.addXYZ(88.5, 39.5, 218.5, 180, 0), Team.RED));
        options.add(new FlagSpawnPointOption(loc.addXYZ(88.5, 39.5, 218.5, 180, 0), Team.RED));

        options.add(new GateOption(loc.addXYZ(42, 39, -52), loc.addXYZ(47, 45, -52)));
        options.add(new GateOption(loc.addXYZ(69, 35, -52), loc.addXYZ(75, 43, -52)));
        options.add(new GateOption(loc.addXYZ(70, 36, 195), loc.addXYZ(75, 43, 195)));
        options.add(new GateOption(loc.addXYZ(97, 39, 195), loc.addXYZ(104, 45, 195)));

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
