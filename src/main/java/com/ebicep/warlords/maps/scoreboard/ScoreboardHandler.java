package com.ebicep.warlords.maps.scoreboard;

import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.player.WarlordsPlayer;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

/**
 *
 */
public interface ScoreboardHandler {
    public int getRedPriority();
    public int getBluePriority();
    public default int getPriority(@Nonnull Team team) {
        switch(team) {
            case RED:
                return getRedPriority();
            case BLUE:
                return getBluePriority();
            default:
                throw new AssertionError("Only team RED/BLUE supported");
        }
    }
    public @Nonnull List<String> computeLines(@Nonnull WarlordsPlayer player);
    public @Nonnull Runnable registerChangeHandler(@Nonnull Consumer<ScoreboardHandler> onChange);
}
