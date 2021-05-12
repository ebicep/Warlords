package com.ebicep.warlords.util;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.Commands;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class RemoveEntities extends Commands {

    public void onRemove() {

        Bukkit.getWorlds().stream().skip(1).forEachOrdered(world -> {
            world.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
        });

        // add more later
    }
}

