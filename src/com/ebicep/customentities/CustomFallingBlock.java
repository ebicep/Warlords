package com.ebicep.customentities;

import com.ebicep.warlords.WarlordsPlayer;
import org.bukkit.entity.FallingBlock;

public class CustomFallingBlock {
    private FallingBlock customFallingBlock;
    private double yLevel;
    private WarlordsPlayer owner;

    public CustomFallingBlock(FallingBlock customFallingBlock, double yLevel, WarlordsPlayer owner) {
        this.customFallingBlock = customFallingBlock;
        this.yLevel = yLevel;
        this.owner = owner;
    }

    public FallingBlock getFallingBlock() {
        return customFallingBlock;
    }

    public void setFallingBlock(FallingBlock fallingBlock) {
        this.customFallingBlock = fallingBlock;
    }

    public double getyLevel() {
        return yLevel;
    }

    public void setyLevel(double yLevel) {
        this.yLevel = yLevel;
    }

    public FallingBlock getCustomFallingBlock() {
        return customFallingBlock;
    }

    public void setCustomFallingBlock(FallingBlock customFallingBlock) {
        this.customFallingBlock = customFallingBlock;
    }

    public WarlordsPlayer getOwner() {
        return owner;
    }

    public void setOwner(WarlordsPlayer owner) {
        this.owner = owner;
    }
}
