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

public class Crossfire extends GameMap {

    public Crossfire() {
        super(
                "Crossfire",
                32,
                12,
                60 * SECOND,
                "Crossfire",
                3,
                GameMode.CAPTURE_THE_FLAG
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(215.5, 37, 109.5), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(7.5, 37, 19.5, 180, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(158.5, 6.5, 28.5), PowerupOption.PowerUp.ENERGY));
        options.add(new PowerupOption(loc.addXYZ(65.5, 6.5, 98.5), PowerupOption.PowerUp.ENERGY));

        options.add(new PowerupOption(loc.addXYZ(217.5, 36.5, 89.5), PowerupOption.PowerUp.SPEED));
        options.add(new PowerupOption(loc.addXYZ(6.5, 36.5, 39.5), PowerupOption.PowerUp.SPEED));

        options.add(new PowerupOption(loc.addXYZ(96.5, 6.5, 108.5), PowerupOption.PowerUp.HEALING));
        options.add(new PowerupOption(loc.addXYZ(127.5, 6.5, 19.5), PowerupOption.PowerUp.HEALING));

        options.add(SpawnpointOption.forTeam(loc.addXYZ(133, 11.5, 130.5, 125, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(91, 11.5, -2.5, -55, 0), Team.RED));

        options.add(new FlagCapturePointOption(loc.addXYZ(217.5, 36.5, 126.5, 150, 0), Team.BLUE));
        options.add(new FlagSpawnPointOption(loc.addXYZ(217.5, 36.5, 126.5, 150, 0), Team.BLUE));

        options.add(new FlagCapturePointOption(loc.addXYZ(5.5, 36.5, 1.5, -25, 0), Team.RED));
        options.add(new FlagSpawnPointOption(loc.addXYZ(5.5, 36.5, 1.5, -25, 0), Team.RED));

        options.add(new GateOption(loc.addXYZ(203, 36, 119), loc.addXYZ(203, 42, 124)));
        options.add(new GateOption(loc.addXYZ(227, 36, 109), loc.addXYZ(227, 40, 115)));
        options.add(new GateOption(loc.addXYZ(19, 36, 4), loc.addXYZ(19, 40, 9)));
        options.add(new GateOption(loc.addXYZ(-3, 36, 14), loc.addXYZ(-3, 40, 18)));

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
