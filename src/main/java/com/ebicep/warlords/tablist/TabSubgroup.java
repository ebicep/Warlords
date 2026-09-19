package com.ebicep.warlords.tablist;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Ordered entry sources within a {@link TabGroup}. Features/Options own the instance and call
 * {@link #markDirty()} / {@link #markDirty(UUID)} like scoreboard handlers.
 */
public class TabSubgroup {

    private final int priority;
    private final List<TabEntrySource> sources = new ArrayList<>();
    private boolean globalDirty;
    private final Set<UUID> playerDirty = new HashSet<>();

    public TabSubgroup(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Nonnull
    public List<TabEntrySource> getSources() {
        return Collections.unmodifiableList(sources);
    }

    public TabSubgroup add(@Nonnull TabEntrySource source) {
        sources.add(Objects.requireNonNull(source, "source"));
        markDirty();
        return this;
    }

    public TabSubgroup addAll(@Nonnull List<TabEntrySource> toAdd) {
        for (TabEntrySource source : toAdd) {
            sources.add(Objects.requireNonNull(source, "source"));
        }
        markDirty();
        return this;
    }

    public void clearSources() {
        if (sources.isEmpty()) {
            return;
        }
        sources.clear();
        markDirty();
    }

    public void setSources(@Nonnull List<TabEntrySource> next) {
        sources.clear();
        for (TabEntrySource source : next) {
            sources.add(Objects.requireNonNull(source, "source"));
        }
        markDirty();
    }

    /**
     * Content is not viewer-specific — all active viewers in the manager need a flush.
     */
    public void markDirty() {
        globalDirty = true;
    }

    /**
     * Viewer-specific content changed for one player.
     */
    public void markDirty(@Nonnull UUID viewer) {
        playerDirty.add(Objects.requireNonNull(viewer, "viewer"));
    }

    public boolean isGlobalDirty() {
        return globalDirty;
    }

    @Nonnull
    public Set<UUID> getPlayerDirtyViewers() {
        return Collections.unmodifiableSet(playerDirty);
    }

    /**
     * Clears the global dirty flag after the manager has enqueued all viewers for flush.
     */
    public boolean consumeGlobalDirty() {
        if (!globalDirty) {
            return false;
        }
        globalDirty = false;
        return true;
    }

    void clearPlayerDirty(@Nonnull UUID viewer) {
        playerDirty.remove(viewer);
    }
}
