package com.ebicep.warlords.maps;

import com.ebicep.warlords.commands.Commands;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;

public class SpawnFlag extends Commands {

    public void spawnFlag(GameManager.GameMap map) {
        Location blueFlagLocation = map.map.getBlueFlag().clone();
        blueFlagLocation.setWorld(Bukkit.getWorld(map.map.mapName));
        Block block = blueFlagLocation.getWorld().getBlockAt(blueFlagLocation);
        block.setType(Material.STANDING_BANNER);

        ArmorStand blueFlag = blueFlagLocation.getWorld().spawn(blueFlagLocation, ArmorStand.class);
        blueFlag.setGravity(false);
        blueFlag.setCanPickupItems(false);
        blueFlag.setCustomName("§9BLU FLAG");
        blueFlag.setCustomNameVisible(true);
        blueFlag.setVisible(false);

        Location redFlagLocation = map.map.getBlueFlag().clone();
        blueFlagLocation.setWorld(Bukkit.getWorld(map.map.mapName));
        Block block2 = redFlagLocation.getWorld().getBlockAt(redFlagLocation);
        block2.setType(Material.STANDING_BANNER);

        ArmorStand redFlag = redFlagLocation.getWorld().spawn(redFlagLocation, ArmorStand.class);
        redFlag.setGravity(false);
        redFlag.setCanPickupItems(false);
        redFlag.setCustomName("§cRED FLAG");
        redFlag.setCustomNameVisible(true);
        redFlag.setVisible(false);
    }
}
