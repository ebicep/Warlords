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
import com.ebicep.warlords.game.option.raid.RaidOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;

import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class TheObsidianTrailRaid extends GameMap {

    public TheObsidianTrailRaid() {
        super(
                "§c§lThe Obsidian Trail",
                8,
                4,
                60 * SECOND,
                "TheObsidianTrail",
                1,
                GameMode.RAID
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(711.5, 7, 179.5), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(711.5, 7, 179.5), Team.RED).asOption());

        options.add(SpawnpointOption.forTeam(loc.addXYZ(711.5, 7, 179.5), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(711.5, 7, 179.5), Team.RED));

        options.add(new RaidOption());
        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());
        options.add(new BoundingBoxOption(loc.getWorld()));

        return options;
    }

}