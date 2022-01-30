package com.ebicep.warlords.maps.option.marker.scoreboard;

import com.ebicep.warlords.maps.option.marker.GameMarker;
import com.ebicep.warlords.player.WarlordsPlayer;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ScoreboardHandler extends GameMarker {

    /**
     * Gets the priority for the score listing. Scoreboard handlers with a lower
     * score are shown on top
     *
     * @param player The player to compute it for
     * @return The priority for this player
     */
    public int getPriority(@Nonnull WarlordsPlayer player);

    /**
     * Computes the list of lines for the scoreboard
     *
     * @param player The player to compute it for
     * @return The new scoreboard lines
     */
    @Nonnull
    public List<String> computeLines(@Nonnull WarlordsPlayer player);

    /**
     * Registers an onchange handler for the scoreboard, scoreboard should call
     * all onchange handlers if their content changes. This prevent unneeded
     * re-rendering
     *
     * @param onChange The onchange handler to register
     * @return An function to unregister this registration
     */
    @Nonnull
    public Runnable registerChangeHandler(@Nonnull Consumer<ScoreboardHandler> onChange);

    /**
     * Gets the group. Scoreboard are typically grouped per owner in the
     * overview
     *
     * @return The group, or null if there is no owner
     * @implNote Classes processing this should see every null as an unique group
     */
    @Nullable
    public String getGroup();
}
