package com.ebicep.warlords.util.bukkit;

import com.ebicep.warlords.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

public class RemoveEntities {

    public static void doRemove(Game game) {
        // TODO support multiple games in the same world by adding a bounding box to every map
        game.getLocations().getWorld().getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
    }

    /**
     * Checks any world for orphan horses, and kill them
     */
    public static void removeHorsesInGame() {
        Bukkit.getWorlds().stream().skip(1).forEachOrdered(world -> {
            world.getEntities().stream().filter(entity -> (entity instanceof Horse && entity.getPassenger() == null)).forEach(Entity::remove);
        });
    }

    public static void removeArmorStands(int worldSkips) {
        Bukkit.getWorlds().stream().skip(worldSkips).forEach(world -> world.getEntities().stream().filter(entity -> (entity instanceof ArmorStand)).forEach(Entity::remove));
    }
}

