package com.ebicep.warlords.maps;

import com.ebicep.warlords.commands.Commands;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBanner;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.material.Banner;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

public class SpawnFlag extends Commands {

    public void spawnFlag(GameMap map) {
        Location blueFlagLocation = map.getBlueFlag().clone();
        blueFlagLocation.setWorld(Bukkit.getWorld(map.mapName));
        Block block = blueFlagLocation.getWorld().getBlockAt(blueFlagLocation);
        block.setType(Material.STANDING_BANNER);
        MaterialData newData = block.getState().getData();
        ((Banner) newData).setFacingDirection(getFacingDirection(block));
        block.setData(newData.getData());

        ArmorStand blueFlag = blueFlagLocation.getWorld().spawn(blueFlagLocation, ArmorStand.class);
        blueFlag.setGravity(false);
        blueFlag.setCanPickupItems(false);
        blueFlag.setCustomName("§9BLU FLAG");
        blueFlag.setCustomNameVisible(true);
        blueFlag.setVisible(false);


        Location redFlagLocation = map.getRedFlag().clone();
        blueFlagLocation.setWorld(Bukkit.getWorld(map.mapName));
        Block block2 = redFlagLocation.getWorld().getBlockAt(redFlagLocation);
        block2.setType(Material.STANDING_BANNER);
        MaterialData newData2 = block2.getState().getData();
        ((Banner) newData2).setFacingDirection(getFacingDirection(block2));
        block2.setData(newData2.getData());

        ArmorStand redFlag = redFlagLocation.getWorld().spawn(redFlagLocation, ArmorStand.class);
        redFlag.setGravity(false);
        redFlag.setCanPickupItems(false);
        redFlag.setCustomName("§cRED FLAG");
        redFlag.setCustomNameVisible(true);
        redFlag.setVisible(false);
    }

    private BlockFace getFacingDirection(Block banner) {
        Location location = banner.getLocation();
        if (banner.getWorld().getBlockAt(location.clone().add(0, 0, -5)).getType() == Material.AIR) {
            return BlockFace.NORTH;
        }
        if (banner.getWorld().getBlockAt(location.clone().add(-5, 0, 0)).getType() == Material.AIR) {
            return BlockFace.WEST;
        }
        if (banner.getWorld().getBlockAt(location.clone().add(5, 0, 0)).getType() == Material.AIR) {
            return BlockFace.EAST;
        }
        return BlockFace.SOUTH;
    }

}
