
package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@FunctionalInterface
public interface TeamMarker extends GameMarker {
    public EnumSet<Team> getTeams();
    
    public static TeamMarker create(Team ... teams) {
        EnumSet<Team> asList = teams.length == 0 ? EnumSet.noneOf(Team.class) : EnumSet.copyOf(Arrays.asList(teams));
        return () -> asList;
    }
    
    public static EnumSet<Team> getTeams(Game game) {
        EnumSet<Team> teams;
        boolean mayModify;
        
        List<TeamMarker> markers = game.getMarkers(TeamMarker.class);
        switch(markers.size()) {
            case 0:
                teams = EnumSet.noneOf(Team.class);
                mayModify = true;
                break;
            case 1:
                teams = markers.get(0).getTeams();
                mayModify = false;
                break;
            default:
                teams = EnumSet.noneOf(Team.class);
                mayModify = true;
                for (TeamMarker marker : markers) {
                    teams.addAll(marker.getTeams());
                }
        }
        if (teams.isEmpty()) {
            if(!mayModify) {
                teams = EnumSet.noneOf(Team.class);
            }
            teams.add(Team.BLUE);
        }
        return teams;
    }
}
