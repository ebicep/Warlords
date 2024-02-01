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
import com.ebicep.warlords.game.option.pvp.DuelsTeleportOption;
import com.ebicep.warlords.game.option.respawn.RespawnWaveOption;
import com.ebicep.warlords.game.option.win.MercyWinOption;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.game.option.win.WinByPointsOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;

import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class HeavensWill extends GameMap {

    public HeavensWill() {
        super(
                "Heaven's Will",
                2,
                2,
                60 * SECOND,
                "Heaven",
                1,
                GameMode.DUEL
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(726.5, 9, 176.5).yaw(-140), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(756.5, 8, 143.5).yaw(40), Team.RED).asOption());

        options.add(new GateOption(loc, 723, 6, 173, 729, 13, 179));
        options.add(new GateOption(loc, 753, 6, 140, 759, 13, 146));

        options.add(SpawnpointOption.forTeam(loc.addXYZ(726.5, 9, 176.5).yaw(-140), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(756.5, 8, 143.5).yaw(40), Team.RED));

        options.add(new WinByPointsOption(5));
        options.add(new MercyWinOption(3, 5));
        options.add(new WinAfterTimeoutOption(300));
        options.add(new AbstractScoreOnEventOption.OnKill(1));
        options.add(new RespawnWaveOption(1, 2, 3));
        options.add(new GraveOption());
        options.add(new DuelsTeleportOption());

        options.add(new BasicScoreboardOption());
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

        return options;
    }

}