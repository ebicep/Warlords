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
import com.ebicep.warlords.game.option.pve.treasurehunt.DungeonRoomMarker;
import com.ebicep.warlords.game.option.pve.treasurehunt.RoomType;
import com.ebicep.warlords.game.option.pve.treasurehunt.TreasureHuntOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import org.bukkit.Location;

import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class DualDescent extends GameMap {

    public DualDescent() {
        super(
                "Dual Descent",
                2,
                1,
                60 * SECOND,
                "TreasureHuntMap",
                3,
                GameMode.TREASURE_HUNT
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(255, 42, 255), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(255, 42, 255), Team.RED).asOption());

        options.add(SpawnpointOption.forTeam(loc.addXYZ(255, 42, 255), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(255, 42, 255), Team.RED));

        options.add(new BoundingBoxOption(new Location(loc.getWorld(), 0, 32, 0), new Location(loc.getWorld(), 511, 255, 511)));
        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                32, -48, -48,
                47, -33, -33,
                RoomType.START,
                true,
                true,
                true,
                true
        ).asOption());

        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                32, -48, -16,
                47, -33, -1,
                RoomType.END,
                true,
                true,
                true,
                true
        ).asOption());

        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                32, -48, 16,
                47, -33, 31,
                RoomType.TREASURE,
                true,
                true,
                true,
                true
        ).asOption());

        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                0, -48, 16,
                15, -33, 31,
                RoomType.NORMAL,
                false,
                true,
                false,
                true
        ).asOption());

        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                0, -48, -16,
                15, -33, -1,
                RoomType.NORMAL,
                true,
                false,
                true,
                false
        ).asOption());

        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                -32, -48, -48,
                -17, -33, -33,
                RoomType.NORMAL,
                false,
                false,
                true,
                true
        ).asOption());

        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                0, -48, -48,
                15, -33, -33,
                RoomType.NORMAL,
                false,
                true,
                true,
                false
        ).asOption());

        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                0, -48, -80,
                15, -33, -65,
                RoomType.NORMAL,
                true,
                true,
                true,
                false
        ).asOption());

        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                -17, -48, -79,
                -32, -33, -64,
                RoomType.DEAD_END,
                false,
                false,
                true,
                false
        ).asOption());

        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                -64, -48, -79,
                -49, -33, -64,
                RoomType.DEAD_END,
                true,
                false,
                false,
                false
        ).asOption());

        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                -96, -48, -79,
                -81, -33, -64,
                RoomType.DEAD_END,
                false,
                false,
                false,
                true
        ).asOption());

        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                -126, -48, -79,
                -111, -33, -64,
                RoomType.DEAD_END,
                false,
                true,
                false,
                false
        ).asOption());

        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                32, -48, -81,
                47, -33, -66,
                RoomType.NORMAL,
                true,
                true,
                true,
                true
        ).asOption());

        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                32, -48, -113,
                47, -33, -98,
                RoomType.NORMAL,
                false,
                true,
                true,
                true
        ).asOption());

        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                32, -48, -145,
                47, -33, -130,
                RoomType.NORMAL,
                true,
                false,
                true,
                true
        ).asOption());

        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                -64, -48, -48,
                -49, -33, -33,
                RoomType.NORMAL,
                true,
                true,
                false,
                false
        ).asOption());

        options.add(DungeonRoomMarker.create(
                loc.getWorld(),
                32, -48, -177,
                47, -33, -162,
                RoomType.NORMAL,
                true,
                true,
                false,
                true
        ).asOption());

        options.add(new GraveOption());
        options.add(new BasicScoreboardOption());
        options.add(new TreasureHuntOption(200));

        return options;
    }

}
