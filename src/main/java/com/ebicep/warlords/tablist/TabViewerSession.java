package com.ebicep.warlords.tablist;

import com.ebicep.warlords.util.bukkit.packets.tablist.TabListEntry;
import com.ebicep.warlords.util.bukkit.packets.tablist.TabListPackets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-viewer custom tab session: rendered slots + real players hidden via {@code listed=false}.
 * <p>
 * Fake slot UUIDs stay stable for safe remove/add. GameProfile names use the real player's
 * username when {@link TabEntry#logicalId()} maps to an online player (so chat Tab-complete
 * suggests real names), otherwise the slot's invisible decorative name. Tab grid order uses
 * packet {@code listOrder} (slot index), not scoreboard teams — so CustomScoreboard nametag
 * teams on real names stay compatible.
 */
public class TabViewerSession {

    private static final Map<UUID, TabViewerSession> ACTIVE_BY_VIEWER = new ConcurrentHashMap<>();

    private final UUID viewerId;
    private boolean active;
    private final Map<Integer, TabEntry> rendered = new HashMap<>();
    /** Last GameProfile name sent per slot (for name-change rebuilds). */
    private final Map<Integer, String> sentProfileNames = new HashMap<>();
    private final Set<UUID> hiddenReals = new HashSet<>();

    public TabViewerSession(@Nonnull UUID viewerId) {
        this.viewerId = Objects.requireNonNull(viewerId, "viewerId");
    }

    /**
     * Whether this viewer currently has an active custom tab (used by packet rewrite).
     */
    public static boolean isCustomTabActive(@Nonnull UUID viewerId) {
        return ACTIVE_BY_VIEWER.containsKey(viewerId);
    }

    @Nonnull
    public UUID getViewerId() {
        return viewerId;
    }

    public boolean isActive() {
        return active;
    }

    @Nullable
    public Player getViewer() {
        return Bukkit.getPlayer(viewerId);
    }

    /**
     * Hide all currently online real players from this viewer's tab list.
     */
    public void activateAndHideReals() {
        Player viewer = getViewer();
        if (viewer == null) {
            return;
        }
        setActive(true);
        for (Player online : Bukkit.getOnlinePlayers()) {
            hideReal(viewer, online.getUniqueId(), false);
        }
    }

    /**
     * Hide a real player on active sessions. Sends {@code listed=false} for players already known
     * to the client; new joins are forced unlisted by the PLAYER_INFO_UPDATE rewrite.
     */
    public void hideRealIfActive(@Nonnull UUID realPlayerId) {
        if (!active) {
            return;
        }
        Player viewer = getViewer();
        if (viewer == null) {
            return;
        }
        hideReal(viewer, realPlayerId, true);
    }

    private void setActive(boolean value) {
        active = value;
        if (value) {
            ACTIVE_BY_VIEWER.put(viewerId, this);
        } else {
            ACTIVE_BY_VIEWER.remove(viewerId, this);
        }
    }

    private void hideReal(Player viewer, UUID realPlayerId, boolean force) {
        if (!force && hiddenReals.contains(realPlayerId)) {
            return;
        }
        Player real = Bukkit.getPlayer(realPlayerId);
        if (real == null) {
            return;
        }
        TabListPackets.updateListed(viewer, TabListEntry.of(realPlayerId, real.getName()).withListed(false));
        hiddenReals.add(realPlayerId);
    }

    /**
     * Restore listed=true for still-online hidden reals and remove fake slots.
     */
    public void deactivate() {
        deactivate(true);
    }

    /**
     * Clear fake slots and optionally restore {@code listed=true} for hidden reals.
     * Pass {@code restoreListed=false} when handing off to another custom tab scope
     * so the client never flashes the vanilla player list.
     */
    public void deactivate(boolean restoreListed) {
        Player viewer = getViewer();
        if (viewer != null) {
            if (restoreListed) {
                restoreHidden(viewer);
            } else {
                hiddenReals.clear();
            }
            clearRendered(viewer);
        } else {
            hiddenReals.clear();
            rendered.clear();
            sentProfileNames.clear();
        }
        setActive(false);
    }

    private void restoreHidden(Player viewer) {
        if (hiddenReals.isEmpty()) {
            return;
        }
        for (UUID realId : hiddenReals) {
            Player real = Bukkit.getPlayer(realId);
            if (real == null) {
                continue;
            }
            TabListPackets.updateListed(viewer, TabListEntry.of(realId, real.getName()).withListed(true));
        }
        hiddenReals.clear();
    }

    public void onRealQuit(@Nonnull UUID realPlayerId) {
        hiddenReals.remove(realPlayerId);
    }

    @Nonnull
    public Map<Integer, TabEntry> getRendered() {
        return rendered;
    }

    /**
     * Diff {@code next} against the last render and send batched packets.
     * Slot order is driven by packet {@code listOrder} (= slot index).
     */
    public void applyLayout(@Nonnull TabLayoutEngine.LayoutResult layout) {
        Player viewer = getViewer();
        if (viewer == null) {
            return;
        }
        if (!active) {
            activateAndHideReals();
        }

        Map<Integer, TabEntry> next = new HashMap<>();
        for (TabLayoutEngine.PlacedEntry placed : layout.entries()) {
            next.put(placed.slotIndex(), placed.entry());
        }

        Set<Integer> allSlots = new HashSet<>();
        allSlots.addAll(rendered.keySet());
        allSlots.addAll(next.keySet());

        List<TabListEntry> toAdd = new ArrayList<>();
        List<TabListEntry> displayUpdates = new ArrayList<>();
        List<TabListEntry> latencyUpdates = new ArrayList<>();
        List<UUID> toRemove = new ArrayList<>();
        List<TabListEntry> rebuild = new ArrayList<>();
        Map<Integer, String> nextProfileNames = new HashMap<>();

        for (int slotIndex : allSlots) {
            TabSlot slot = TabSlot.get(slotIndex);
            TabEntry oldEntry = rendered.get(slotIndex);
            TabEntry newEntry = next.get(slotIndex);
            String oldProfileName = sentProfileNames.get(slotIndex);

            if (newEntry == null) {
                if (oldEntry != null) {
                    toRemove.add(slot.uuid());
                }
                continue;
            }

            String profileName = resolveProfileName(slot, newEntry);
            nextProfileNames.put(slotIndex, profileName);
            TabListEntry packetEntry = toPacketEntry(slot, newEntry, profileName);

            if (oldEntry == null) {
                toAdd.add(packetEntry);
            } else if (!oldEntry.sameSkin(newEntry) || !Objects.equals(oldProfileName, profileName)) {
                toRemove.add(slot.uuid());
                rebuild.add(packetEntry);
            } else {
                if (!Objects.equals(oldEntry.displayName(), newEntry.displayName())) {
                    displayUpdates.add(packetEntry);
                }
                if (oldEntry.latency() != newEntry.latency()) {
                    latencyUpdates.add(packetEntry);
                }
            }
        }

        if (!toRemove.isEmpty()) {
            TabListPackets.remove(viewer, toRemove);
        }
        if (!toAdd.isEmpty()) {
            TabListPackets.add(viewer, toAdd);
        }
        if (!rebuild.isEmpty()) {
            TabListPackets.add(viewer, rebuild);
        }
        if (!displayUpdates.isEmpty()) {
            TabListPackets.updateDisplayName(viewer, displayUpdates);
        }
        if (!latencyUpdates.isEmpty()) {
            TabListPackets.updateLatency(viewer, latencyUpdates);
        }

        rendered.clear();
        rendered.putAll(next);
        sentProfileNames.clear();
        sentProfileNames.putAll(nextProfileNames);
    }

    private void clearRendered(Player viewer) {
        if (rendered.isEmpty()) {
            return;
        }
        List<UUID> ids = new ArrayList<>(rendered.size());
        for (Integer slotIndex : rendered.keySet()) {
            ids.add(TabSlot.get(slotIndex).uuid());
        }
        TabListPackets.remove(viewer, ids);
        rendered.clear();
        sentProfileNames.clear();
    }

    @Nonnull
    static String resolveProfileName(@Nonnull TabSlot slot, @Nonnull TabEntry entry) {
        Player online = resolveLogicalPlayer(entry.logicalId());
        if (online != null) {
            return online.getName();
        }
        return slot.profileName();
    }

    @Nullable
    private static Player resolveLogicalPlayer(@Nullable String logicalId) {
        if (logicalId == null || logicalId.isEmpty()) {
            return null;
        }
        try {
            return Bukkit.getPlayer(UUID.fromString(logicalId));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    static TabListEntry toPacketEntry(TabSlot slot, TabEntry entry, String profileName) {
        TabListEntry packet = TabListEntry.of(slot.uuid(), profileName, entry.displayName(), entry.latency())
                .withListOrder(slot.index());
        if (entry.skinTexture() != null) {
            packet = packet.withSkin(entry.skinTexture(), entry.skinSignature());
        }
        return packet;
    }
}
