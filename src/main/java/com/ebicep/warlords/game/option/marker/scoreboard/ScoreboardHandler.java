package com.ebicep.warlords.game.option.marker.scoreboard;

import com.ebicep.warlords.game.option.marker.GameMarker;
import com.ebicep.warlords.player.WarlordsPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public interface ScoreboardHandler extends GameMarker {

    /**
     * Gets the priority for the score listing. Scoreboard handlers with a lower
     * score are shown on top
     *
     * @param player The player to compute it for
     * @return The priority for this player
     */
    int getPriority(@Nullable WarlordsPlayer player);

    /**
     * Computes the list of lines for the scoreboard
     *
     * @param player The player to compute it for
     * @return The new scoreboard lines
     */
    @Nonnull
    List<String> computeLines(@Nullable WarlordsPlayer player);

    /**
     * Registers an onchange handler for the scoreboard, scoreboard should call
     * all onchange handlers if their content changes. This prevent unneeded
     * re-rendering
     *
     * @param onChange The onchange handler to register
     * @return An function to unregister this registration
     */
    @Nonnull
    Runnable registerChangeHandler(@Nonnull Consumer<ScoreboardHandler> onChange);

    /**
     * Gets the group. Scoreboard are typically grouped per owner in the
     * overview
     *
     * @return The group, or null if there is no owner
     * @implNote Classes processing this should see every null as an unique group
     */
    @Nullable
    String getGroup();
}
