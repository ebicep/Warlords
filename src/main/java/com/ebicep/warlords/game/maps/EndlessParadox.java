package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.BasicScoreboardOption;
import com.ebicep.warlords.game.option.GraveOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.SpawnpointOption;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.pve.anomaly.AnomalyOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class EndlessParadox extends GameMap {

    public EndlessParadox() {
        super(
                "Endless Paradox",
                4,
                1,
                60 * SECOND,
                "EndlessParadox",
                3,
                GameMode.ANOMALY
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(0, 90, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(0, 90, 0), Team.RED).asOption());

        options.add(SpawnpointOption.forTeam(loc.addXYZ(0, 90, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(0, 90, 0), Team.RED));

        options.add(new BoundingBoxOption(new Location(loc.getWorld(), 0, 0, 0), new Location(loc.getWorld(), 1024, 255, 1024)));

        options.add(new GraveOption());
        options.add(new BasicScoreboardOption());
        options.add(new AnomalyOption());

        return options;
    }
}
