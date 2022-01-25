package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.maps.Team;
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
}
