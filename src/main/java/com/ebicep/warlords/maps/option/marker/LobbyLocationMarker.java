package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.util.Pair;
import com.ebicep.warlords.util.Utils;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collector;
import javax.annotation.Nullable;
import org.bukkit.Location;

public interface LobbyLocationMarker extends LocationMarker {
    public boolean matchesTeam(Team team);
    
    public static LobbyLocationMarker create(Location location) {
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
    
    public static LobbyLocationMarker create(Location location, Team t) {
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
    public static LobbyLocationMarker getRandomLobbyLocation(Game game, Team team) {
        return getLobbyLocation(game, team, Utils.randomElement(), Objects::nonNull);
    }
    
    @Nullable
    public static LobbyLocationMarker getFirstLobbyLocation(Game game, Team team) {
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
    
    public static <T> T getLobbyLocation(Game game, Team team, Collector<LobbyLocationMarker, ?, T> collector, Predicate<T> isAcceptable) {
        List<LobbyLocationMarker> lobbies = game.getMarkers(LobbyLocationMarker.class);
        T location = lobbies.stream().filter(e -> e.matchesTeam(team)).collect(collector);
        if (!isAcceptable.test(location)) {
            location = lobbies.stream().collect(collector); 
        }
        return location;
    }
}
