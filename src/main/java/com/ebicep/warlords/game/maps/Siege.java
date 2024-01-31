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

public class Siege extends GameMap {

    public Siege() {
        super(
                "Siege",
                32,
                12,
                60 * SECOND,
                "Siege",
                1,
                GameMode.TEAM_DEATHMATCH
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(72.5, 65.5, -60.5), PowerupOption.PowerUp.ENERGY, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(39.5, 64.5, 58.5), PowerupOption.PowerUp.SPEED, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(66.5, 63.5, 0.5), PowerupOption.PowerUp.SPEED, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(145.5, 80.5, 0.5), PowerupOption.PowerUp.SPEED, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-20.5, 65.5, 0.5), PowerupOption.PowerUp.SPEED, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-54.5, 83.5, 63.5), PowerupOption.PowerUp.SPEED, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-25.5, 63.5, -35.5), PowerupOption.PowerUp.ENERGY, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-25.5, 64.5, 32.5), PowerupOption.PowerUp.ENERGY, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(100.5, 79.5, 11.5), PowerupOption.PowerUp.ENERGY, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(-54.5, 83.5, -62.5), PowerupOption.PowerUp.SPEED, 45, 30));

        options.add(new PowerupOption(loc.addXYZ(23.5, 62.5, 45.5), 45, 45));
        options.add(new PowerupOption(loc.addXYZ(23.5, 65.5, -50.5), 45, 45));
        options.add(new PowerupOption(loc.addXYZ(23.5, 62.5, 7.5), 45, 45));
        options.add(new PowerupOption(loc.addXYZ(23.5, 62.5, -23.5), 45, 45));

        options.add(new PowerupOption(loc.addXYZ(-14.5, 84.5, -14.0), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(-14.5, 84.5, 15.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(83.5, 65.5, 64.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(100.5, 79.5, -10.5), PowerupOption.PowerUp.HEALING, 45, 60));

        options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED));

        options.add(new GateOption(loc.addXYZ(83, 84, 4), loc.addXYZ(83, 79, -4)));
        options.add(new GateOption(loc.addXYZ(-70, 83, 3), loc.addXYZ(-70, 86, -2), Material.IRON_BARS));

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
