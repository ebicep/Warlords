package com.ebicep.customentities;

import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

public class CustomFallingBlock {
    private FallingBlock customFallingBlock;
    private double yLevel;
    private Player owner;
    private AbstractAbility ability;
    private int ticksLived = 0;

    public CustomFallingBlock(FallingBlock customFallingBlock, double yLevel, Player owner, AbstractAbility ability) {
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

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
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
