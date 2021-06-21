package com.ebicep.warlords.maps;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class Gates {
    public static int changeGate(GameMap map, Cuboid gate, boolean open) {
        Material search = open ? Material.FENCE : Material.AIR;
        Material replace = open ? Material.AIR : Material.FENCE;
        int changed = 0;
        for(int x = gate.getMinX(); x <= gate.getMaxX(); x++) {
            for(int z = gate.getMinZ(); z <= gate.getMaxZ(); z++) {
                for(int y = gate.getMinY(); y <= gate.getMaxY(); y++) {
                    Block block = gate.getWorld().getBlockAt(x, y, z);
                    if(block.getType() == search) {
                        block.setType(replace);
                        changed++;
                    }
                }
            }
        }
        return changed;
    }

    public static void changeGates(GameMap map, boolean open) {
        for(Cuboid gate : map.getFenceGates()) {
            changeGate(map, gate, open);
        }
    }
}