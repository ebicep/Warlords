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
import com.ebicep.warlords.game.option.pve.raid.Raid;
import com.ebicep.warlords.game.option.pve.raid.RaidOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;

import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class RaidOne extends GameMap {

    public RaidOne() {
        super(
                "Regnum of Two Crowns",
                8,
                1,
                60 * SECOND,
                "RaidOne",
                1,
                GameMode.RAID
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-50.5, 25, -450.5), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-50.5, 25, -450.5), Team.RED).asOption());

        options.add(SpawnpointOption.forTeam(loc.addXYZ(-50.5, 25, -450.5), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-50.5, 25, -450.5), Team.RED));

        //options.add(new RaidOption(Raid.REGNUM_OF_TWO_CROWNS));
        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());
        options.add(new BoundingBoxOption(loc.getWorld()));

        return options;
    }

}
