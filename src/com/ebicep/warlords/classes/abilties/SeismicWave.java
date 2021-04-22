package com.ebicep.warlords.classes.abilties;

import org.bukkit.entity.FallingBlock;

public class SeismicWave {

    FallingBlock block;

    public SeismicWave(FallingBlock block) {
        this.block = block;
    }

    public FallingBlock getBlock() {
        return block;
    }

    public void setBlock(FallingBlock block) {
        this.block = block;
    }


}
