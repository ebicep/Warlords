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

public class BlackTemple extends GameMap {

    public BlackTemple() {
        super(
                "Black Temple",
                32,
                12,
                60 * SECOND,
                "BlackTemple",
                1,
                GameMode.TEAM_DEATHMATCH
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-0.5, 26.5, 105.5, -180, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(3.5, 26.5, -103.5, 0, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(94.5, 27.5, 7.5), PowerupOption.PowerUp.SPEED, 45, 0));
        options.add(new PowerupOption(loc.addXYZ(33.5, 27.5, 16.5), PowerupOption.PowerUp.SPEED, 45, 0));
        options.add(new PowerupOption(loc.addXYZ(-29.5, 27.5, 16.5), PowerupOption.PowerUp.SPEED, 45, 0));
        options.add(new PowerupOption(loc.addXYZ(-90.5, 27.5, -5.5), PowerupOption.PowerUp.SPEED, 45, 0));

        options.add(new PowerupOption(loc.addXYZ(33.5, 27.5, -14.5), PowerupOption.PowerUp.ENERGY, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(94.5, 27.5, -5.5), PowerupOption.PowerUp.ENERGY, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-29.5, 27.5, -14.5), PowerupOption.PowerUp.ENERGY, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-90.5, 27.5, 7.5), PowerupOption.PowerUp.ENERGY, 45, 30));

        options.add(new PowerupOption(loc.addXYZ(56.5, 30.5, 0.5), 45, 45));
        options.add(new PowerupOption(loc.addXYZ(-52.5, 30.5, 0.5), 45, 45));
        options.add(new PowerupOption(loc.addXYZ(4.5, 31.5, -3.5), PowerupOption.PowerUp.ENERGY, 45, 45));
        options.add(new PowerupOption(loc.addXYZ(0.5, 31.5, 6.5), PowerupOption.PowerUp.ENERGY, 45, 45));

        options.add(new PowerupOption(loc.addXYZ(60.5, 27.5, -28.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(-55.5, 27.5, -27.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(59.5, 27.5, 27.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(-55.5, 27.5, 27.5), PowerupOption.PowerUp.HEALING, 45, 60));

        options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED));

        options.add(new GateOption(loc.addXYZ(19, 26, -93), loc.addXYZ(19, 30, -102), Material.IRON_BARS));

        options.add(new GateOption(loc.addXYZ(727, 67, 437), loc.addXYZ(727, 64, 441)));

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
