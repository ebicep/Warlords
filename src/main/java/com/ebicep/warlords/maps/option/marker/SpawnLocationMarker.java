package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.maps.Team;

public interface SpawnLocationMarker extends LocationMarker {
    public double getPriority(Team team);
}
