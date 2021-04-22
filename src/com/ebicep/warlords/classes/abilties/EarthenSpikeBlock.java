package com.ebicep.warlords.classes.abilties;

import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

public class EarthenSpikeBlock {

    FallingBlock block;
    Player player;

    public EarthenSpikeBlock(FallingBlock block, Player player) {
        this.block = block;
        this.player = player;
    }

    public FallingBlock getBlock() {
        return block;
    }

    public void setBlock(FallingBlock block) {
        this.block = block;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
