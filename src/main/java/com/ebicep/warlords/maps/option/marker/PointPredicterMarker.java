package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.maps.Team;

public interface PointPredicterMarker extends GameMarker {
    double predictPointsNextMinute(Team team);
}
