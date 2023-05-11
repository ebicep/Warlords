package com.ebicep.holograms;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HologramVisibility {

    private final Map<UUID, Visibility> playerVisibility = new HashMap<>();

    public void setPlayerVisibility(UUID uuid, Visibility visibility) {
        playerVisibility.put(uuid, visibility);
    }

    public void setPlayerVisibility(Player player, Visibility visibility) {
        setPlayerVisibility(player.getUniqueId(), visibility);
    }

    enum Visibility {
        SHOWN,
        HIDDEN,
    }

}
