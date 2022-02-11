package com.ebicep.warlords.game.option.marker;

import com.ebicep.warlords.game.Team;

public interface PointPredicterMarker extends GameMarker {
    double predictPointsNextMinute(Team team);
}
