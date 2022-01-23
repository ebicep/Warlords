
package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.maps.Team;
import java.util.Arrays;
import java.util.EnumSet;

@FunctionalInterface
public interface TeamMarker extends GameMarker {
    public EnumSet<Team> getTeams();
    
    public static TeamMarker create(Team ... teams) {
        EnumSet<Team> asList = teams.length == 0 ? EnumSet.noneOf(Team.class) : EnumSet.copyOf(Arrays.asList(teams));
        return () -> asList;
    }
}
