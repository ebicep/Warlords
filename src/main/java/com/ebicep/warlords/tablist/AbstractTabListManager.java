package com.ebicep.warlords.tablist;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Shared register / dirty-poll / layout / diff loop for lobby and game scopes.
 */
public abstract class AbstractTabListManager {

    public static final int UPDATE_INTERVAL = 10;

    private final Map<String, TabGroup> groupsByName = new LinkedHashMap<>();
    private final Map<UUID, TabViewerSession> sessions = new HashMap<>();
    private final Set<UUID> dirtyViewers = new HashSet<>();
    private int updateTick;

    @Nonnull
    public TabGroup group(
            @Nonnull String name,
            int priority,
            int minColumns,
            int maxColumns,
            int maxEntries
    ) {
        TabGroup existing = groupsByName.get(name);
        if (existing != null) {
            return existing;
        }
        TabGroup created = new TabGroup(name, priority, minColumns, maxColumns, maxEntries);
        groupsByName.put(name, created);
        markAllDirty();
        return created;
    }

    @Nullable
    public TabGroup getGroup(@Nonnull String name) {
        return groupsByName.get(name);
    }

    @Nonnull
    public List<TabGroup> getGroups() {
        return List.copyOf(groupsByName.values());
    }

    public boolean removeGroup(@Nonnull String name) {
        TabGroup removed = groupsByName.remove(name);
        if (removed != null) {
            markAllDirty();
            return true;
        }
        return false;
    }

    /**
     * Online viewers currently managed by this scope (players + spectators for games).
     */
    @Nonnull
    protected abstract Collection<UUID> activeViewerIds();

    @Nullable
    protected abstract Player resolvePlayer(@Nonnull UUID uuid);

    public void addViewer(@Nonnull UUID viewerId) {
        sessions.computeIfAbsent(viewerId, TabViewerSession::new);
        dirtyViewers.add(viewerId);
    }

    public void removeViewer(@Nonnull UUID viewerId) {
        TabViewerSession session = sessions.remove(viewerId);
        if (session != null) {
            session.deactivate();
        }
        dirtyViewers.remove(viewerId);
        for (TabGroup group : groupsByName.values()) {
            for (TabSubgroup subgroup : group.getSubgroups()) {
                subgroup.clearPlayerDirty(viewerId);
            }
        }
    }

    public void onRealPlayerJoin(@Nonnull UUID realPlayerId) {
        for (TabViewerSession session : sessions.values()) {
            session.hideRealIfActive(realPlayerId);
        }
    }

    public void onRealPlayerQuit(@Nonnull UUID realPlayerId) {
        for (TabViewerSession session : sessions.values()) {
            session.onRealQuit(realPlayerId);
        }
        removeViewer(realPlayerId);
    }

    public void markAllDirty() {
        dirtyViewers.addAll(activeViewerIds());
        dirtyViewers.addAll(sessions.keySet());
    }

    public void markDirty(@Nonnull UUID viewer) {
        dirtyViewers.add(viewer);
    }

    /**
     * Poll dirty subgroups, enqueue viewers, then flush viewers in the current UUID hash bucket.
     */
    public void tick() {
        syncViewers();
        drainSubgroupDirty();

        if (dirtyViewers.isEmpty()) {
            updateTick++;
            return;
        }

        int bucket = Math.floorMod(updateTick++, UPDATE_INTERVAL);
        List<UUID> due = new ArrayList<>();
        for (UUID viewerId : dirtyViewers) {
            if (inBucket(viewerId, bucket)) {
                due.add(viewerId);
            }
        }

        List<TabGroup> groups = getGroups();
        for (UUID viewerId : due) {
            TabViewerSession session = sessions.computeIfAbsent(viewerId, TabViewerSession::new);
            Player player = resolvePlayer(viewerId);
            if (player == null) {
                dirtyViewers.remove(viewerId);
                continue;
            }
            TabLayoutEngine.LayoutResult layout = TabLayoutEngine.pack(groups, viewerId);
            session.applyLayout(layout);
            for (TabGroup group : groups) {
                for (TabSubgroup subgroup : group.getSubgroups()) {
                    subgroup.clearPlayerDirty(viewerId);
                }
            }
            dirtyViewers.remove(viewerId);
        }
    }

    private void syncViewers() {
        Set<UUID> active = new HashSet<>(activeViewerIds());
        for (UUID id : List.copyOf(sessions.keySet())) {
            if (!active.contains(id)) {
                removeViewer(id);
            }
        }
        for (UUID id : active) {
            if (!sessions.containsKey(id)) {
                addViewer(id);
            }
        }
    }

    private void drainSubgroupDirty() {
        boolean anyGlobal = false;
        for (TabGroup group : groupsByName.values()) {
            for (TabSubgroup subgroup : group.getSubgroups()) {
                if (subgroup.consumeGlobalDirty()) {
                    anyGlobal = true;
                }
                dirtyViewers.addAll(subgroup.getPlayerDirtyViewers());
            }
        }
        if (anyGlobal) {
            markAllDirty();
        }
    }

    private static boolean inBucket(UUID uuid, int bucket) {
        return Math.floorMod(uuid.hashCode(), UPDATE_INTERVAL) == bucket;
    }

    /**
     * Deactivate all sessions and clear registration. Called on game close / plugin disable.
     */
    public void clear() {
        for (TabViewerSession session : List.copyOf(sessions.values())) {
            session.deactivate();
        }
        sessions.clear();
        dirtyViewers.clear();
        groupsByName.clear();
    }

    @Nullable
    public TabViewerSession getSession(@Nonnull UUID viewerId) {
        return sessions.get(viewerId);
    }
}
