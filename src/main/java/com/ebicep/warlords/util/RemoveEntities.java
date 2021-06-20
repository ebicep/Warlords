package com.ebicep.warlords.util;

import com.ebicep.warlords.commands.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

public class RemoveEntities extends Commands {

    public static void onRemove() {

        Bukkit.getWorlds().stream().skip(1).forEachOrdered(world -> {
            world.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
        });

        // add more later
    }

    public static void removeHorsesInGame() {
        Bukkit.getWorlds().stream().skip(1).forEachOrdered(world -> {
            world.getEntities().stream().filter(entity -> (entity instanceof Horse && entity.getPassenger() == null)).forEach(Entity::remove);
        });
    }
}

