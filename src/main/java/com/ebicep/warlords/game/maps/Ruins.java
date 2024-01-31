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
import com.ebicep.warlords.game.option.pvp.GameOvertimeOption;
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

public class Ruins extends GameMap {

    public Ruins() {
        super(
                "Ruins",
                32,
                12,
                60 * SECOND,
                "Ruins",
                1,
                GameMode.TEAM_DEATHMATCH
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-139.5, 39.5, 0.5, -90, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(144.5, 37.5, 0.5, 90, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(0.5, 43.5, 32.5), PowerupOption.PowerUp.SPEED, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(0.5, 43.5, -31.5), PowerupOption.PowerUp.SPEED, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(0.5, 38.5, 8.5), PowerupOption.PowerUp.ENERGY, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(0.5, 38.5, -7.5), PowerupOption.PowerUp.ENERGY, 45, 30));

        options.add(new PowerupOption(loc.addXYZ(14.5, 35.5, -24.5), 45, 45));
        options.add(new PowerupOption(loc.addXYZ(-13.5, 35.5, 25.5), 45, 45));

        options.add(new PowerupOption(loc.addXYZ(0.5, 25.5, 0.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(122.5, 45.5, 52.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(122.5, 45.5, -51.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(-126.5, 45.5, -51.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(-126.5, 45.5, 52.5), PowerupOption.PowerUp.HEALING, 45, 60));

        options.add(SpawnpointOption.forTeam(loc.addXYZ(-139.5, 39.5, 0.5, -90, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(144.5, 37.5, 0.5, 90, 0), Team.RED));

        options.add(new GateOption(loc.addXYZ(-131, 40, -4), loc.addXYZ(-131, 48, 5), Material.IRON_BARS));
        options.add(new GateOption(loc.addXYZ(131, 37, -4), loc.addXYZ(131, 45, 5), Material.IRON_BARS));

        options.add(new WinByPointsOption(1000));
        options.add(new MercyWinOption());
        if (addons.contains(GameAddon.DOUBLE_TIME)) {
            options.add(new WinAfterTimeoutOption(1800));
        } else {
            options.add(new WinAfterTimeoutOption());
        }
        options.add(new GameOvertimeOption());
        options.add(new AbstractScoreOnEventOption.OnKill(15));
        options.add(new RespawnWaveOption());
        options.add(new RespawnProtectionOption());
        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

        return options;
    }


}
