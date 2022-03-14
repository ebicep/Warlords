package com.ebicep.warlords.game.option.marker;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.MarkerOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collector;

public interface LobbyLocationMarker extends LocationMarker {
    boolean matchesTeam(Team team);
    
    static LobbyLocationMarker create(Location location) {
        return new LobbyLocationMarker() {
            @Override
            public boolean matchesTeam(Team team) {
                return true;
            }

            @Override
            public Location getLocation() {
                return location;
            }
        };
    }
    
    static LobbyLocationMarker create(Location location, Team t) {
        return new LobbyLocationMarker() {
            @Override
            public boolean matchesTeam(Team team) {
                return t == team;
            }

            @Override
            public Location getLocation() {
                return location;
            }
        };
    }
    
    @Nullable
    static LobbyLocationMarker getRandomLobbyLocation(Game game, Team team) {
        return getLobbyLocation(game, team, Utils.randomElement(), Objects::nonNull);
    }
    
    @Nullable
    static LobbyLocationMarker getFirstLobbyLocation(Game game, Team team) {
        return getLobbyLocation(game, team, Collector.of(
                () -> new Pair<LobbyLocationMarker, Void>(null, null),
                (a, b) -> {
                    if(a.getA() == null) {
                        a.setA(b);
                    }
                },
                (a, b) -> a.getA() == null ? b : a,
                (a) -> a.getA()
        ), Objects::nonNull);
    }
    
    static <T> T getLobbyLocation(Game game, Team team, Collector<LobbyLocationMarker, ?, T> collector, Predicate<T> isAcceptable) {
        List<LobbyLocationMarker> lobbies = game.getMarkers(LobbyLocationMarker.class);
        T location = lobbies.stream().filter(e -> e.matchesTeam(team)).collect(collector);
        if (!isAcceptable.test(location)) {
            location = lobbies.stream().collect(collector); 
        }
        return location;
    }
    
    public default Option asOption() {
        return new MarkerOption(this);
    }
}
