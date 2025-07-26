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
import com.ebicep.warlords.game.option.win.MercyWinOption;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.game.option.win.WinByPointsOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;

import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class StormWind2 extends GameMap {

    public StormWind2() {
        super(
                "Storm Wind 2",
                32,
                12,
                60 * SECOND,
                "Stormwind2",
                1,
                GameMode.CAPTURE_THE_FLAG
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(101, 58, 72, 145, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-86, 58, -57, -45, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(63.5, 54.5, -2.5), PowerupOption.PowerUp.ENERGY));
        options.add(new PowerupOption(loc.addXYZ(-47.5, 54.5, 18.5), PowerupOption.PowerUp.ENERGY));

        options.add(new PowerupOption(loc.addXYZ(101.5, 54.5, 38.5), PowerupOption.PowerUp.SPEED));
        options.add(new PowerupOption(loc.addXYZ(-85.5, 54.5, -22.5), PowerupOption.PowerUp.SPEED));

        options.add(new PowerupOption(loc.addXYZ(-28.5, 54.5, 62.5), PowerupOption.PowerUp.HEALING));
        options.add(new PowerupOption(loc.addXYZ(44.5, 54.5, -46.5), PowerupOption.PowerUp.HEALING));

        options.add(SpawnpointOption.forTeam(loc.addXYZ(43, 58, 84.5, 135, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-27, 58, -68.5, -45, 0), Team.RED));

        options.add(new FlagCapturePointOption(loc.addXYZ(89.5, 58, 66.5, 90, 0), Team.BLUE));
        options.add(new FlagSpawnPointOption(loc.addXYZ(89.5, 58, 66.5, 90, 0), Team.BLUE));

        options.add(new FlagCapturePointOption(loc.addXYZ(-73.5, 58, -50.5, -90, 0), Team.RED));
        options.add(new FlagSpawnPointOption(loc.addXYZ(-73.5, 58, -50.5, -90, 0), Team.RED));

        //blue gates
        options.add(new GateOption(loc.addXYZ(83, 58, 79), loc.addXYZ(83, 61, 84)));
        options.add(new GateOption(loc.addXYZ(85, 54, 59), loc.addXYZ(84, 56, 59)));
        options.add(new GateOption(loc.addXYZ(121, 58, 51), loc.addXYZ(113, 64, 51)));
        options.add(new GateOption(loc.addXYZ(97, 58, 50), loc.addXYZ(95, 61, 50)));
        options.add(new GateOption(loc.addXYZ(84, 58, 58), loc.addXYZ(84, 64, 53)));
        //red gates
        options.add(new GateOption(loc.addXYZ(-68, 58, -64), loc.addXYZ(-68, 61, -69)));
        options.add(new GateOption(loc.addXYZ(-70, 54, -44), loc.addXYZ(-69, 56, -44)));
        options.add(new GateOption(loc.addXYZ(-106, 58, -36), loc.addXYZ(-98, 64, -36)));
        options.add(new GateOption(loc.addXYZ(-80, 58, -35), loc.addXYZ(-82, 61, -35)));
        options.add(new GateOption(loc.addXYZ(-69, 58, -43), loc.addXYZ(-69, 64, -38)));

        options.add(new WinByPointsOption());
        options.add(new MercyWinOption());
        WinAfterTimeoutOption timeoutOption = new WinAfterTimeoutOption();
        options.add(timeoutOption);
        if (addons.contains(GameAddon.DOUBLE_TIME)) {
            timeoutOption.setTimeRemaining(timeoutOption.getTimeRemaining() * 2);
        }
        options.add(new GameOvertimeOption());
        options.add(new AbstractScoreOnEventOption.FlagCapture());
        options.add(new AbstractScoreOnEventOption.OnKill());
        options.add(new RespawnProtectionOption());
        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

        return options;
    }

}
