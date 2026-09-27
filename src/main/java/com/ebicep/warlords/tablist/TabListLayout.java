package com.ebicep.warlords.tablist;

import com.ebicep.warlords.game.Game;

import javax.annotation.Nonnull;

/**
 * Installs and refreshes tab-list content for a game. Modes pick a layout when constructing
 * {@link com.ebicep.warlords.game.option.CustomTabListOption}; other Options may still attach
 * their own {@link TabSubgroup}s via the manager after register.
 */
public interface TabListLayout {

    /**
     * Create groups/subgroups once when the manager is constructed.
     */
    void apply(@Nonnull GameTabListManager manager, @Nonnull Game game);

    /**
     * Refresh dynamic content (e.g. team player rows) before pack/flush.
     */
    void sync(@Nonnull GameTabListManager manager, @Nonnull Game game);

    /**
     * Force the next {@link #sync} to rebuild player rows even if the roster UUID set is unchanged
     * (e.g. class / level / flag changed via scoreboard tab-name dirty marks).
     */
    default void forcePlayerSync() {
    }
}
