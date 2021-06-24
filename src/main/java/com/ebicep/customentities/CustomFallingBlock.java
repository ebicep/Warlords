package com.ebicep.customentities;

import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.entity.FallingBlock;

public class CustomFallingBlock {
    private FallingBlock customFallingBlock;
    private double yLevel;
    private WarlordsPlayer owner;
    private AbstractAbility ability;
    private int ticksLived = 0;

    public CustomFallingBlock(FallingBlock customFallingBlock, double yLevel, WarlordsPlayer owner, AbstractAbility ability) {
        this.customFallingBlock = customFallingBlock;
        this.yLevel = yLevel;
        this.owner = owner;
        this.ability = ability;
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

    public AbstractAbility getAbility() {
        return ability;
    }

    public void setAbility(AbstractAbility ability) {
        this.ability = ability;
    }

    public int getTicksLived() {
        return ticksLived;
    }

    public void setTicksLived(int ticksLived) {
        this.ticksLived = ticksLived;
    }
}
