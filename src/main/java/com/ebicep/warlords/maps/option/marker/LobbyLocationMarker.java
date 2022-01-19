package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.maps.Team;

public interface LobbyLocationMarker extends LocationMarker {
    public boolean matchesTeam(Team team);
}
