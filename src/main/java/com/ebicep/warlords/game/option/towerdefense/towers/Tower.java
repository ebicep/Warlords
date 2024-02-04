package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.util.bukkit.LocationBuilder;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public interface Tower {

    /**
     * @param frontRightCorner ALWAYS BUILD TOWER FACING SOUTH AT FRONT RIGHT CORNER or else directions of some blocks will be wrong
     * @param data             3d array of block data
     * @return
     */
    static Block[][][] build(Location frontRightCorner, BlockData[][][] data) {
        LocationBuilder builder = new LocationBuilder(frontRightCorner)
                .pitch(0)
                .yaw((float) (Math.round(frontRightCorner.getYaw() / 90) * 90));
        // build one strip going up at a time
        int maxX = data.length;
        int maxZ = data[0].length;
        int maxY = data[0][0].length;
        Block[][][] builtBlocks = new Block[maxX][maxZ][maxY];
        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < maxX; x++) {
                for (int z = 0; z < maxZ; z++) {
                    BlockData blockData = data[x][z][y];
                    Block block = builder.clone()
                                         .forward(z)
                                         .addY(y)
                                         .left(x)
                                         .getBlock();
                    block.setBlockData(blockData);
                    builtBlocks[x][z][y] = block;

                    // move entities up if in the way
                    if (y != maxY - 1) {
                        block.getLocation()
                             .toCenterLocation()
                             .getNearbyEntities(.5, .5, .5)
                             .forEach(entity -> entity.teleport(entity.getLocation().add(0, 1, 0)));
                    }
                }
            }
        }
        return builtBlocks;
    }

    /**
     * Facing from track to frontLeftCorner
     */
    void build(Location frontLeftCorner);

    default void onRemove() {

    }

    Block[][][] getBuiltBlocks();

    BlockData[][][] getBlockData();

    TowerRegistry getTowerRegistry();

    // TODO
    default int getSize() {
        return getBlockData().length;
    }


}
