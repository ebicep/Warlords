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

public class FalstadGate extends GameMap {

    public FalstadGate() {
        super(
                "Falstad Gate",
                32,
                12,
                60 * SECOND,
                "FalstadGate",
                1,
                GameMode.TEAM_DEATHMATCH
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(93.5, 50.5, 0.5, 90, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 50.5, 0.5, -90, 0), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(78.5, 47.5, 18.5), PowerupOption.PowerUp.SPEED, 45, 0));
        options.add(new PowerupOption(loc.addXYZ(78.5, 47.5, -17.5), PowerupOption.PowerUp.SPEED, 45, 0));
        options.add(new PowerupOption(loc.addXYZ(0.5, 32.5, 0.5), PowerupOption.PowerUp.SPEED, 45, 0));
        options.add(new PowerupOption(loc.addXYZ(-77.5, 47.5, -17.5), PowerupOption.PowerUp.SPEED, 45, 0));
        options.add(new PowerupOption(loc.addXYZ(-77.5, 47.5, 18.5), PowerupOption.PowerUp.SPEED, 45, 0));

        options.add(new PowerupOption(loc.addXYZ(0.5, 57.5, -39.5), PowerupOption.PowerUp.ENERGY, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(0.5, 34.5, 23.5), PowerupOption.PowerUp.ENERGY, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(0.5, 34.5, -22.5), PowerupOption.PowerUp.ENERGY, 45, 30));
        options.add(new PowerupOption(loc.addXYZ(0.5, 57.5, 40.5), PowerupOption.PowerUp.ENERGY, 45, 30));

        options.add(new PowerupOption(loc.addXYZ(0.5, 34.5, -32.5), 45, 45));
        options.add(new PowerupOption(loc.addXYZ(0.5, 34.5, 33.5), 45, 45));
        options.add(new PowerupOption(loc.addXYZ(-4.5, 47.5, 33.5), 45, 45));
        options.add(new PowerupOption(loc.addXYZ(5.5, 47.5, -32.5), 45, 45));

        options.add(new PowerupOption(loc.addXYZ(0.5, 53.5, 0.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(-25.5, 32.5, 0.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(26.5, 32.5, 0.5), PowerupOption.PowerUp.HEALING, 45, 60));
        options.add(new PowerupOption(loc.addXYZ(0.5, 47.5, 0.5), PowerupOption.PowerUp.HEALING, 45, 60));


        options.add(SpawnpointOption.forTeam(loc.addXYZ(93.5, 50.5, 0.5, 90, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 50.5, 0.5, -90, 0), Team.RED));

        options.add(new GateOption(loc.addXYZ(-85, 50, -5), loc.addXYZ(-85, 53, 6), Material.IRON_BARS));
        options.add(new GateOption(loc.addXYZ(85, 50, -5), loc.addXYZ(85, 53, 6), Material.IRON_BARS));

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
