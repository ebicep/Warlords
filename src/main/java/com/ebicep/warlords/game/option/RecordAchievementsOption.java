package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.util.warlords.GameRunnable;

import javax.annotation.Nonnull;

public class RecordAchievementsOption implements Option {

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {

            @Override
            public void run() {
                /*game.warlordsPlayers().forEach(warlordsPlayer -> {
                    //TODO filter out achievements already got
//                    for (WarlordsPlayerAchievements value : WarlordsPlayerAchievements.values()) {
//                        if(value.predicate.test(warlordsPlayer)) {
//                            //give acheivement
//                        }
//                    }
                });*/
            }
        }.runTaskTimer(0, 20);
    }
}
