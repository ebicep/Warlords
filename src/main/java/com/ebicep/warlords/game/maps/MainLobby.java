package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.*;
import com.ebicep.warlords.game.option.DummySpawnOption;
import com.ebicep.warlords.game.option.LobbyGameOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.SpawnpointOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.state.State;
import com.ebicep.warlords.game.state.SyncTimerState;
import com.ebicep.warlords.util.bukkit.LocationFactory;

import java.util.EnumSet;
import java.util.List;

public class MainLobby extends GameMap {

    public MainLobby() {
        super(
                "MainLobby",
                32,
                1,
                0,
                "MainLobby",
                1,
                GameMode.LOBBY
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-60.5, 60, 83, 113, 0), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-60.5, 60, 83, 113, 0), Team.RED).asOption());

        options.add(SpawnpointOption.forTeam(loc.addXYZ(-60.5, 60, 83, 113, 0), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-60.5, 60, 83, 113, 0), Team.RED));

//            LocationBuilder dummySpawnLocation = loc.addXYZ(-78, 60, 78); TODO
//            Collection<Location> dummyWanderLocations = Arrays.asList(
//                    loc.addXYZ(-64.5, 60, 78.5),
//                    loc.addXYZ(-73.5, 60, 80.5),
//                    loc.addXYZ(-71.5, 60, 70.5),
//                    loc.addXYZ(-83, 60, 75),
//                    loc.addXYZ(-89.5, 60, 80.5),
//                    loc.addXYZ(-84, 60, 84.5)
//            );
//            for (int i = 0; i < 4; i++) {
//                options.add(new DummySpawnOption(dummyWanderLocations, i % 2 == 0 ? Team.BLUE : Team.RED, warlordsNPC -> {
//                    NPC npc = warlordsNPC.getNpc();
//                    Waypoints waypoints = npc.getOrAddTrait(Waypoints.class);
//                    waypoints.setWaypointProvider("wander");
//                    WanderWaypointProvider provider = (WanderWaypointProvider) waypoints.getCurrentProvider();
//                    provider.addRegionCentres(dummyWanderLocations);
//                    provider.setXYRange(10, 0);
//                }));
//            }
        options.add(new DummySpawnOption(loc.addXYZ(-65.5, 60, 71.5, 42.5f, 0), Team.BLUE));
        options.add(new DummySpawnOption(loc.addXYZ(-80.5, 60, 73, -32.5f, 0), Team.RED));
        options.add(new DummySpawnOption(loc.addXYZ(-88, 60, 84, -114.5f, 0), Team.BLUE));
        options.add(new DummySpawnOption(loc.addXYZ(-72, 60, 87, 164.5f, 0), Team.RED));

        options.add(new LobbyGameOption());

        return options;
    }

    @Override
    public State initialState(Game game) {
        return new SyncTimerState(game);
    }
}
