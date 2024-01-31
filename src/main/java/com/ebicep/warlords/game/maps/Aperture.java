package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.*;
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

public class Aperture extends GameMap {

    public Aperture() {
        super(
                "Aperture",
                32,
                12,
                60 * SECOND,
                "Aperture",
                3,
                GameMode.CAPTURE_THE_FLAG
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(513.5, 37.5, 243.5, -90, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(687.5, 37.5, 231.5, 90, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(608.5, 16.5, 280.5), PowerupOption.PowerUp.ENERGY));
        options.add(new PowerupOption(loc.addXYZ(592.5, 16.5, 194.5), PowerupOption.PowerUp.ENERGY));

        options.add(new PowerupOption(loc.addXYZ(551.5, 31.5, 217.5), PowerupOption.PowerUp.SPEED));
        options.add(new PowerupOption(loc.addXYZ(649.5, 31.5, 257.5), PowerupOption.PowerUp.SPEED));

        options.add(new PowerupOption(loc.addXYZ(577.5, 22.5, 286.5), PowerupOption.PowerUp.HEALING));
        options.add(new PowerupOption(loc.addXYZ(623.5, 22.5, 188.5), PowerupOption.PowerUp.HEALING));

        options.add(SpawnpointOption.forTeam(loc.addXYZ(560.5, 32.5, 294.5, -140, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(640.5, 32.5, 180.5, 40, 0), Team.RED));

        options.add(new FlagCapturePointOption(loc.addXYZ(522.5, 37.5, 233.5, 90, 0), Team.BLUE));
        options.add(new FlagSpawnPointOption(loc.addXYZ(522.5, 37.5, 233.5, 90, 0), Team.BLUE));

        options.add(new FlagCapturePointOption(loc.addXYZ(678.5, 37.5, 241.1, -90, 0), Team.RED));
        options.add(new FlagSpawnPointOption(loc.addXYZ(678.5, 37.5, 241.1, -90, 0), Team.RED));

        options.add(new GateOption(loc.addXYZ(665, 36, 228), loc.addXYZ(665, 40, 232)));
        options.add(new GateOption(loc.addXYZ(683, 36, 252), loc.addXYZ(690, 40, 252)));
        options.add(new GateOption(loc.addXYZ(663, 42, 212), loc.addXYZ(663, 46, 215)));

        options.add(new GateOption(loc.addXYZ(535, 36, 242), loc.addXYZ(535, 40, 247)));
        options.add(new GateOption(loc.addXYZ(510, 36, 222), loc.addXYZ(518, 40, 222)));
        options.add(new GateOption(loc.addXYZ(537, 42, 262), loc.addXYZ(537, 46, 259)));

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
        options.add(new BoundingBoxOption(loc.getWorld()));

        return options;
    }


}
