package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.player.WarlordsPlayer;
import java.util.List;
import java.util.function.Consumer;


public class FlagSpawnPointOption implements Option {

    @Override
    public void register(Game game) {
        
        game.registerScoreboardHandler(new ScoreboardHandler() {
            @Override
            public List<String> computeLines(WarlordsPlayer player) {
                
            }

            @Override
            public Runnable registerChangeHandler(Consumer<ScoreboardHandler> onChange) {
                
            }
            
            
        });
    }

    @Override
    public int tick(Game game) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
	
}
