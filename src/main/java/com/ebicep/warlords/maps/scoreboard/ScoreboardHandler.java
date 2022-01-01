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
    /**
     * Gets the priority for the score listing. Scoreboard handlers with a lower score are shown on top
     * @param player The player to compute it for
     * @return The priority for this player
     */
    public int getPriority(@Nonnull WarlordsPlayer player);
    /**
     * Computes the list of lines for the scoreboard
     * @param player The player to compute it for
     * @return 
     */
    public @Nonnull List<String> computeLines(@Nonnull WarlordsPlayer player);
    public @Nonnull Runnable registerChangeHandler(@Nonnull Consumer<ScoreboardHandler> onChange);
}
