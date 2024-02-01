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

public class Practice extends GameMap {

    public Practice() {
        super(
                "Practice",
                300,
                1,
                60 * SECOND,
                "WLDebug",
                3,
                GameMode.DEBUG
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(727.5, 8.5, 200.5), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(727.5, 8.5, 196.5), Team.RED).asOption());

        options.add(new DummySpawnOption(loc.addXYZ(720.5, 7, 206.5), Team.RED));
        options.add(new DummySpawnOption(loc.addXYZ(703.5, 7, 206.5), Team.BLUE));

        options.add(new PowerupOption(loc.addXYZ(713.5, 8.5, 209.5), PowerupOption.PowerUp.SELF_DAMAGE, 5, 5));
        options.add(new PowerupOption(loc.addXYZ(710.5, 8.5, 209.5), PowerupOption.PowerUp.SELF_HEAL, 5, 5));

        options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 188.5), PowerupOption.PowerUp.DAMAGE, 5, 5));
        options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 192.5), PowerupOption.PowerUp.ENERGY, 5, 5));
        options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 196.5), PowerupOption.PowerUp.SPEED, 5, 5));
        options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 200.5), PowerupOption.PowerUp.HEALING, 5, 5));

        options.add(SpawnpointOption.forTeam(loc.addXYZ(727.5, 8.5, 196.5), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(727.5, 8.5, 196.5), Team.RED));

        options.add(new FlagCapturePointOption(loc.addXYZ(703.5, 8.5, 212.5), Team.BLUE));
        options.add(new FlagSpawnPointOption(loc.addXYZ(703.5, 8.5, 212.5), Team.BLUE));

        options.add(new FlagCapturePointOption(loc.addXYZ(720.5, 8.5, 212.5), Team.RED));
        options.add(new FlagSpawnPointOption(loc.addXYZ(720.5, 8.5, 212.5), Team.RED));

        options.add(new GateOption(loc.addXYZ(713, 7, 195), loc.addXYZ(713, 10, 198)));

        options.add(new WinByPointsOption(3000));
        options.add(new MercyWinOption(3000, 5));
        if (addons.contains(GameAddon.DOUBLE_TIME)) {
            options.add(new WinAfterTimeoutOption(3600));
        } else {
            options.add(new WinAfterTimeoutOption(1800));
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