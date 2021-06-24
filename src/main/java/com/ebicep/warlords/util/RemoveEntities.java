package com.ebicep.warlords.util;

import com.ebicep.warlords.maps.GameMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class RemoveEntities {

    public static void doRemove(GameMap map) {
        map.getBlueFlag().getWorld().getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
        // add more later
    }
}

