package com.ebicep.warlords.game.option.towerdefense.attributes;


import org.bukkit.block.data.BlockData;

/**
 * Represents a tower that can be broken
 */
public interface Breakable {

    BlockData[][][] getBrokenBlockData();

    boolean isBroken();

}
