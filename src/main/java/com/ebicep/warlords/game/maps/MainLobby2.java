package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.SpawnpointOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.whackamole.Hole;
import com.ebicep.warlords.game.option.whackamole.WhackAMoleOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;

import java.util.EnumSet;
import java.util.List;

public class MainLobby2 extends GameMap {

    public MainLobby2() {
        super(
                "MainLobby",
                1,
                1,
                0,
                "MainLobby",
                1,
                GameMode.WHACK_A_MOLE
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(11.5, 81, 116.5), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(11.5, 81, 116.5), Team.RED).asOption());

        options.add(SpawnpointOption.forTeam(loc.addXYZ(11.5, 81, 116.5), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(11.5, 81, 116.5), Team.RED));


        options.add(new WhackAMoleOption(List.of(
                new Hole(loc.addXYZ(12.5, 79, 115.5)),
                new Hole(loc.addXYZ(11.5, 79, 115.5)),
                new Hole(loc.addXYZ(10.5, 79, 115.5)),

                new Hole(loc.addXYZ(12.5, 79, 116.5)),
                new Hole(loc.addXYZ(11.5, 79, 116.5)),
                new Hole(loc.addXYZ(10.5, 79, 116.5)),

                new Hole(loc.addXYZ(12.5, 79, 117.5)),
                new Hole(loc.addXYZ(11.5, 79, 117.5)),
                new Hole(loc.addXYZ(10.5, 79, 117.5))
        )));

        return options;
    }

}
