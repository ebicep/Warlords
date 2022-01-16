package com.ebicep.warlords.util;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

public class RemoveEntities {

    public static void doRemove(Game game) {
        map.getBlueFlag().getWorld().getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
        map.getRedFlag().getWorld().getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
        // add more later
    }

    public static void removeHorsesInGame() {
        Bukkit.getWorlds().stream().skip(1).forEachOrdered(world -> {
            world.getEntities().stream().filter(entity -> (entity instanceof Horse && entity.getPassenger() == null)).forEach(Entity::remove);
        });
    }

    public static void removeArmorStands(int worldSkips) {
        Bukkit.getWorlds().stream().skip(worldSkips).forEach(world -> world.getEntities().stream().filter(entity -> (entity instanceof ArmorStand)).forEach(Entity::remove));
    }
}

