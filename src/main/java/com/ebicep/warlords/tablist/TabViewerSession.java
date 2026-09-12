package com.ebicep.warlords.tablist;

import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.util.bukkit.packets.tablist.TabListEntry;
import com.ebicep.warlords.util.bukkit.packets.tablist.TabListPackets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Per-viewer custom tab session: rendered slots + real players hidden via {@code listed=false}.
 */
public class TabViewerSession {

    private final UUID viewerId;
    private boolean active;
    private final Map<Integer, TabEntry> rendered = new HashMap<>();
    private final Set<UUID> hiddenReals = new HashSet<>();

    public TabViewerSession(@Nonnull UUID viewerId) {
        this.viewerId = Objects.requireNonNull(viewerId, "viewerId");
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
        active = true;
        for (Player online : Bukkit.getOnlinePlayers()) {
            hideReal(viewer, online.getUniqueId());
        }
    }

    public void hideRealIfActive(@Nonnull UUID realPlayerId) {
        if (!active) {
            return;
        }
        Player viewer = getViewer();
        if (viewer == null) {
            return;
        }
        hideReal(viewer, realPlayerId);
    }

    private void hideReal(Player viewer, UUID realPlayerId) {
        if (hiddenReals.contains(realPlayerId)) {
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
        Player viewer = getViewer();
        if (viewer != null) {
            restoreHidden(viewer);
            clearRendered(viewer);
        } else {
            hiddenReals.clear();
            rendered.clear();
        }
        active = false;
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
     * Diff {@code next} against the last render and send batched packets. Assigns scoreboard teams
     * for slot ordering.
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

        Scoreboard scoreboard = CustomScoreboard.getPlayerScoreboard(viewer).getScoreboard();
        Set<Integer> allSlots = new HashSet<>();
        allSlots.addAll(rendered.keySet());
        allSlots.addAll(next.keySet());

        java.util.List<TabListEntry> toAdd = new java.util.ArrayList<>();
        java.util.List<TabListEntry> displayUpdates = new java.util.ArrayList<>();
        java.util.List<TabListEntry> latencyUpdates = new java.util.ArrayList<>();
        java.util.List<UUID> toRemove = new java.util.ArrayList<>();
        java.util.List<TabListEntry> skinRebuild = new java.util.ArrayList<>();

        for (int slotIndex : allSlots) {
            TabSlot slot = TabSlot.get(slotIndex);
            TabEntry oldEntry = rendered.get(slotIndex);
            TabEntry newEntry = next.get(slotIndex);

            if (newEntry == null) {
                if (oldEntry != null) {
                    toRemove.add(slot.uuid());
                    removeTeamEntry(scoreboard, slot);
                }
                continue;
            }

            ensureTeamEntry(scoreboard, slot);
            TabListEntry packetEntry = toPacketEntry(slot, newEntry);

            if (oldEntry == null) {
                toAdd.add(packetEntry);
            } else if (!oldEntry.sameSkin(newEntry)) {
                toRemove.add(slot.uuid());
                skinRebuild.add(packetEntry);
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
        if (!skinRebuild.isEmpty()) {
            TabListPackets.add(viewer, skinRebuild);
        }
        if (!displayUpdates.isEmpty()) {
            TabListPackets.updateDisplayName(viewer, displayUpdates);
        }
        if (!latencyUpdates.isEmpty()) {
            TabListPackets.updateLatency(viewer, latencyUpdates);
        }

        rendered.clear();
        rendered.putAll(next);
    }

    private void clearRendered(Player viewer) {
        if (rendered.isEmpty()) {
            return;
        }
        Scoreboard scoreboard = CustomScoreboard.getPlayerScoreboard(viewer).getScoreboard();
        java.util.List<UUID> ids = new java.util.ArrayList<>(rendered.size());
        for (Integer slotIndex : rendered.keySet()) {
            TabSlot slot = TabSlot.get(slotIndex);
            ids.add(slot.uuid());
            removeTeamEntry(scoreboard, slot);
        }
        TabListPackets.remove(viewer, ids);
        rendered.clear();
    }

    private static TabListEntry toPacketEntry(TabSlot slot, TabEntry entry) {
        TabListEntry packet = TabListEntry.of(slot.uuid(), slot.profileName(), entry.displayName(), entry.latency());
        if (entry.skinTexture() != null) {
            packet = packet.withSkin(entry.skinTexture(), entry.skinSignature());
        }
        return packet;
    }

    private static void ensureTeamEntry(Scoreboard scoreboard, TabSlot slot) {
        Team team = scoreboard.getTeam(slot.teamName());
        if (team == null) {
            team = scoreboard.registerNewTeam(slot.teamName());
        }
        if (!team.hasEntry(slot.profileName())) {
            team.addEntry(slot.profileName());
        }
    }

    private static void removeTeamEntry(Scoreboard scoreboard, TabSlot slot) {
        Team team = scoreboard.getTeam(slot.teamName());
        if (team != null && team.hasEntry(slot.profileName())) {
            team.removeEntry(slot.profileName());
        }
    }

    public void hideReals(@Nonnull Collection<? extends Player> players) {
        Player viewer = getViewer();
        if (viewer == null || !active) {
            return;
        }
        for (Player player : players) {
            hideReal(viewer, player.getUniqueId());
        }
    }
}
