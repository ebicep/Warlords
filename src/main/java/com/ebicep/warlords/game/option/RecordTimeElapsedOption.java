package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class RecordTimeElapsedOption implements Option {

    private int timeElapsed;
    private static final int SCOREBOARD_PRIORITY = 50;

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {

            @Override
            public void run() {
                timeElapsed++;
            }
        }.runTaskTimer(0, GameRunnable.SECOND);
    }

    @Override
    public void register(@Nonnull Game game) {
        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "spec") {
            @Override
            public List<String> computeLines(@Nullable WarlordsEntity player) {
                return Collections.singletonList("Time: " + ChatColor.GREEN + Utils.formatTimeLeft(timeElapsed));
            }
        });
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
