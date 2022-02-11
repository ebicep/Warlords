
package com.ebicep.warlords.game.option.marker;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.MarkerOption;
import com.ebicep.warlords.game.option.Option;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@FunctionalInterface
public interface TeamMarker extends GameMarker {
    EnumSet<Team> getTeams();
    
    static TeamMarker create(Team... teams) {
        EnumSet<Team> asList = teams.length == 0 ? EnumSet.noneOf(Team.class) : EnumSet.copyOf(Arrays.asList(teams));
        return () -> asList;
    }
    
    static EnumSet<Team> getTeams(Game game) {
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
    
    public default Option asOption() {
        return new MarkerOption(this);
    }
}
