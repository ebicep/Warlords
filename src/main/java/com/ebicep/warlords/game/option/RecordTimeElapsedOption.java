package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.util.warlords.GameRunnable;

import javax.annotation.Nonnull;

public class RecordTimeElapsedOption implements Option {

    private int timeElapsed;

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {

            @Override
            public void run() {
                timeElapsed++;
            }
        }.runTaskTimer(0, GameRunnable.SECOND);
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public static int getTimeElapsed(@Nonnull Game game) {
        for (Option option : game.getOptions()) {
            if (option instanceof RecordTimeElapsedOption) {
                RecordTimeElapsedOption drawAfterTimeoutOption = (RecordTimeElapsedOption) option;
                return drawAfterTimeoutOption.getTimeElapsed();
            }
        }
        return 0;
    }
}
