package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.util.bukkit.LocationBuilder;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

public interface Tower {

    /**
     * @param frontRightCorner ALWAYS BUILD TOWER FACING SOUTH AT FRONT RIGHT CORNER or else directions of some blocks will be wrong
     * @param data             3d array of block data
     */
    static void build(Location frontRightCorner, BlockData[][][] data) {
        LocationBuilder builder = new LocationBuilder(frontRightCorner)
                .pitch(0)
                .yaw((float) (Math.round(frontRightCorner.getYaw() / 90) * 90));
        // build one strip going up at a time
        // TODO tp players if they are in the way
        int maxX = data.length;
        int maxZ = data[0].length;
        int maxY = data[0][0].length;
        for (int up = 0; up < maxY; up++) {
            for (int left = 0; left < maxX; left++) {
                for (int forward = 0; forward < maxZ; forward++) {
                    BlockData blockData = data[left][forward][up];
                    builder.clone()
                           .forward(forward)
                           .addY(up)
                           .left(left)
                           .getBlock()
                           .setBlockData(blockData);
                }
            }
        }
    }

    /**
     * Facing from track to frontLeftCorner
     */
    void build(Location frontLeftCorner);

    TowerRegistry getTowerRegistry();


}
