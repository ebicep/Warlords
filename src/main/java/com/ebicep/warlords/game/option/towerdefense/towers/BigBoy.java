package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.game.option.towerdefense.TowerCache;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BigBoy implements Tower {

    private Block[][][] builtBlocks;

    @Override
    public void build(Location frontLeftCorner) {
        builtBlocks = Tower.build(frontLeftCorner, TowerCache.Tower.BIG_BOY_1.data);
    }

    @Override
    public Block[][][] getBuiltBlocks() {
        return builtBlocks;
    }

    @Override
    public BlockData[][][] getBlockData() {
        return TowerCache.Tower.BIG_BOY_1.data;
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.BIG_BOY;
    }

}
