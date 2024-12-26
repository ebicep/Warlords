package com.ebicep.holograms;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VisibilityManager {

    private VisibilityType visibilityType;
    private final Set<UUID> currentViewers = new HashSet<>();
    private final Set<UUID> viewers = new HashSet<>();

    public VisibilityManager(VisibilityType visibilityType) {
        this.visibilityType = visibilityType;
    }

    public void setVisibilityType(VisibilityType visibilityType) {
        this.visibilityType = visibilityType;
    }

    public VisibilityType getVisibilityType() {
        return visibilityType;
    }

    public Set<UUID> getCurrentViewers() {
        return new HashSet<>(currentViewers);
    }

    public void addCurrentViewer(UUID uuid) {
        currentViewers.add(uuid);
    }

    public void addCurrentViewer(Player player) {
        currentViewers.add(player.getUniqueId());
    }

    public void removeCurrentViewer(UUID uuid) {
        currentViewers.remove(uuid);
    }

    public void removeCurrentViewer(Player player) {
        currentViewers.remove(player.getUniqueId());
    }

    public boolean isCurrentlyVisibleTo(UUID uuid) {
        return currentViewers.contains(uuid);
    }

    public boolean isCurrentlyVisibleTo(Player player) {
        return currentViewers.contains(player.getUniqueId());
    }

    public void addViewer(UUID uuid) {
        viewers.add(uuid);
    }

    public void addViewer(Player player) {
        viewers.add(player.getUniqueId());
    }

    public void removeViewer(UUID uuid) {
        viewers.remove(uuid);
    }

    public void removeViewer(Player player) {
        viewers.remove(player.getUniqueId());
    }

    public boolean isViewer(UUID uuid) {
        return viewers.contains(uuid);
    }

    public boolean isViewer(Player player) {
        return viewers.contains(player.getUniqueId());
    }

}
