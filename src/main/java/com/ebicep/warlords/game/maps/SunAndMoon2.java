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
import com.ebicep.warlords.game.option.pvp.interception.InterceptionPointOption;
import com.ebicep.warlords.game.option.respawn.RespawnProtectionOption;
import com.ebicep.warlords.game.option.respawn.RespawnWaveOption;
import com.ebicep.warlords.game.option.win.MercyWinOption;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.game.option.win.WinByPointsOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import org.bukkit.Material;

import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class SunAndMoon2 extends GameMap {

    public SunAndMoon2() {
        super(
                "Sun and Moon 2",
                32,
                12,
                60 * SECOND,
                "SunAndMoon2",
                1,
                GameMode.CAPTURE_THE_FLAG
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(19.5, 67.5, -133.5, 180, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-18.5, 67.5, 134.5, 0, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(-51.5, 66.5, -61.5), PowerupOption.PowerUp.ENERGY));
        options.add(new PowerupOption(loc.addXYZ(52.5, 66.5, 62.5), PowerupOption.PowerUp.ENERGY));

        options.add(new PowerupOption(loc.addXYZ(67.5, 72.5, -110.5), PowerupOption.PowerUp.SPEED));
        options.add(new PowerupOption(loc.addXYZ(-66.5, 72.5, 111.5), PowerupOption.PowerUp.SPEED));
        options.add(new PowerupOption(loc.addXYZ(-59.5, 75.5, -48.5), PowerupOption.PowerUp.SPEED));
        options.add(new PowerupOption(loc.addXYZ(60.5, 75.5, 49.5), PowerupOption.PowerUp.SPEED));
        options.add(new PowerupOption(loc.addXYZ(-28.5, 66.5, -170.5), PowerupOption.PowerUp.SPEED));
        options.add(new PowerupOption(loc.addXYZ(29.5, 66.5, 171.5), PowerupOption.PowerUp.SPEED));

        options.add(new PowerupOption(loc.addXYZ(60.5, 66.5, -54.5), PowerupOption.PowerUp.HEALING));
        options.add(new PowerupOption(loc.addXYZ(-59.5, 66.5, 55.5), PowerupOption.PowerUp.HEALING));

        options.add(new PowerupOption(loc.addXYZ(0.5, 85.5, 0.5), PowerupOption.PowerUp.COOLDOWN, 60, 60));

        options.add(SpawnpointOption.forTeam(loc.addXYZ(37.5, 65.5, -89.5, 0, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-36.5, 65.5, 90.5, 180, 0), Team.RED));

        options.add(new FlagCapturePointOption(loc.addXYZ(25.5, 70.5, -167.5, 0, 0), Team.BLUE));
        options.add(new FlagSpawnPointOption(loc.addXYZ(25.5, 70.5, -167.5, 0, 0), Team.BLUE));

        options.add(new FlagCapturePointOption(loc.addXYZ(-24.5, 70.5, 168.5, 180, 0), Team.RED));
        options.add(new FlagSpawnPointOption(loc.addXYZ(-24.5, 70.5, 168.5, 180, 0), Team.RED));

        options.add(new AbstractScoreOnEventOption.FlagCapture());

        options.add(new GateOption(loc.addXYZ(23, 68, -129), loc.addXYZ(15, 75, -129), Material.IRON_BARS));
        options.add(new GateOption(loc.addXYZ(14, 68, -130), loc.addXYZ(14, 75, -138), Material.IRON_BARS));
        options.add(new GateOption(loc.addXYZ(15, 68, -139), loc.addXYZ(23, 75, -139), Material.IRON_BARS));
        options.add(new GateOption(loc.addXYZ(24, 68, -138), loc.addXYZ(24, 75, -130), Material.IRON_BARS));

        options.add(new GateOption(loc.addXYZ(-23, 68, 129), loc.addXYZ(-15, 75, 129), Material.IRON_BARS));
        options.add(new GateOption(loc.addXYZ(-14, 68, 130), loc.addXYZ(-14, 75, 138), Material.IRON_BARS));
        options.add(new GateOption(loc.addXYZ(-15, 68, 139), loc.addXYZ(-23, 75, 139), Material.IRON_BARS));
        options.add(new GateOption(loc.addXYZ(-24, 68, 138), loc.addXYZ(-24, 75, 130), Material.IRON_BARS));

        options.add(new WinByPointsOption());
        options.add(new MercyWinOption());
        WinAfterTimeoutOption timeoutOption = new WinAfterTimeoutOption();
        options.add(timeoutOption);
        if (addons.contains(GameAddon.DOUBLE_TIME)) {
            timeoutOption.setTimeRemaining(timeoutOption.getTimeRemaining() * 2);
        }
        options.add(new GameOvertimeOption());
        options.add(new AbstractScoreOnEventOption.OnKill());
        options.add(new RespawnWaveOption());
        options.add(new RespawnProtectionOption());
        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

        return options;
    }

}
