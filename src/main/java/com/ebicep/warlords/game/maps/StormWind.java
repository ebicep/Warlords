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

public class StormWind extends GameMap {

    public StormWind() {
        super(
                "Stormwind",
                28,
                12,
                60 * SECOND,
                "Stormwind",
                1,
                GameMode.TEAM_DEATHMATCH
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(8, 59, 109.5, 180, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(8, 59, -94.5, 0, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(3.5, 61.5, 3.5), PowerupOption.PowerUp.ENERGY, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(12.5, 61.5, 12.5), PowerupOption.PowerUp.ENERGY, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(76.5, 59.5, 56.5), PowerupOption.PowerUp.SPEED, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(72.5, 56.5, -47.5), PowerupOption.PowerUp.SPEED, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-56.5, 56.5, 63.5), PowerupOption.PowerUp.SPEED, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-60.5, 59.5, -40.5), PowerupOption.PowerUp.SPEED, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(83.5, 57.5, -2.5), PowerupOption.PowerUp.ENERGY, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-67.5, 57.5, 18.5), PowerupOption.PowerUp.ENERGY, 45, 30));

        options.add(new PowerupOption(loc.addXYZ(-28.5, 54.5, -27.5), 45, 45));
        options.add(new PowerupOption(loc.addXYZ(-28.5, 54.5, 43.5), 45, 45));
        options.add(new PowerupOption(loc.addXYZ(44.5, 54.5, -27.5), 45, 45));
        options.add(new PowerupOption(loc.addXYZ(44.5, 54.5, 43.5), 45, 45));

        options.add(new PowerupOption(loc.addXYZ(93.5, 54.5, -41.5), PowerupOption.PowerUp.ENERGY, 45, 45));
        options.add(new PowerupOption(loc.addXYZ(-80.5, 59.5, -28.5), PowerupOption.PowerUp.ENERGY, 45, 45));
        options.add(new PowerupOption(loc.addXYZ(-77.5, 54.5, 57.5), PowerupOption.PowerUp.ENERGY, 45, 45));
        options.add(new PowerupOption(loc.addXYZ(96.5, 59.5, 44.5), PowerupOption.PowerUp.HEALING, 45, 45));

        options.add(new PowerupOption(loc.addXYZ(46.5, 56.5, 3.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(75.5, 54.5, -54.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(-30.5, 56.5, 12.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(-59.5, 54.5, 70.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(-47.5, 54.5, -16.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(63.5, 54.5, 32.5), PowerupOption.PowerUp.HEALING, 45, 60));

        options.add(SpawnpointOption.forTeam(loc.addXYZ(8, 59, 109.5, 180, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(8, 59, -94.5, 0, 0), Team.RED));

        options.add(new GateOption(loc.addXYZ(2, 54, 89), loc.addXYZ(14, 58, 89), Material.IRON_BARS));
        options.add(new GateOption(loc.addXYZ(14, 54, -74), loc.addXYZ(2, 58, -74), Material.IRON_BARS));

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
