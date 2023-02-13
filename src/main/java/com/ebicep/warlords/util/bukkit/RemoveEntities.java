package com.ebicep.warlords.util.bukkit;

import com.ebicep.warlords.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RemoveEntities {

    public static void doRemove(Game game) {
        // TODO support multiple games in the same world by adding a bounding box to every map
        List<Entity> collect = game
                .getLocations()
                .getWorld()
                .getEntities()
                .stream()
                .filter(entity -> !(entity instanceof Player))
                .toList();
        collect.forEach(Entity::remove);
    }

    /**
     * Checks any world for orphan horses, and kill them
     */
    public static void removeHorsesInGame() {
        Bukkit.getWorlds()
              .stream()
              .skip(1)
              .forEachOrdered(world -> new ArrayList<>(world.getEntities())
                      .stream()
                      .filter(entity -> (entity instanceof Horse && entity.getPassengers().isEmpty()))
                      .forEach(Entity::remove)
              );
    }

    public static void removeArmorStands(int worldSkips) {
        Bukkit.getWorlds()
              .stream()
              .skip(worldSkips)
              .forEach(world -> new ArrayList<>(world.getEntities())
                      .stream()
                      .filter(entity -> (entity instanceof ArmorStand))
                      .forEach(Entity::remove)
              );
    }
}

