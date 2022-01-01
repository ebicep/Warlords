package com.ebicep.warlords.maps.scoreboard;

import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.player.WarlordsPlayer;


public abstract class SimpleScoreboardHandler extends AbstractScoreboardHandler {
    
    protected int priority;

    public SimpleScoreboardHandler(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority(WarlordsPlayer player) {
        return priority;
    }

}
