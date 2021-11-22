/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebicep.warlords.maps.scoreboard;

import com.ebicep.warlords.player.WarlordsPlayer;
import java.util.List;
import java.util.function.Consumer;


public abstract class BaseScoreboardHandler implements ScoreboardHandler {
    
    private final int getRedPriority

    @Override
    public int getRedPriority() {
    }

    @Override
    public int getBluePriority() {
    }

    @Override
    public Runnable registerChangeHandler(Consumer<ScoreboardHandler> onChange) {
    }
    
    public void markChanged() {
        
    }
	
}
