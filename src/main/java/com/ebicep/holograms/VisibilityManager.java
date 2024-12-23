package com.ebicep.holograms;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VisibilityManager {

    private VisibilityType visibilityType = VisibilityType.ALL;
    private Set<UUID> currentViewers = new HashSet<>();

    public VisibilityManager() {
    }

    public VisibilityManager(VisibilityType visibilityType) {
        this.visibilityType = visibilityType;
    }

    public Set<UUID> getCurrentViewers() {
        return currentViewers;
    }

    public boolean isCurrentlyVisibleTo(UUID uuid) {
        return currentViewers.contains(uuid);
    }

    public boolean isCurrentlyVisibleTo(Player player) {
        return currentViewers.contains(player);
    }

}
