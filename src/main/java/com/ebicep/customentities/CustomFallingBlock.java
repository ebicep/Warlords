package com.ebicep.customentities;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.entity.FallingBlock;

public class CustomFallingBlock {
    private FallingBlock customFallingBlock;
    private WarlordsPlayer owner;
    private AbstractAbility ability;
    private int ticksLived = 0;

    public CustomFallingBlock(FallingBlock customFallingBlock, WarlordsPlayer owner, AbstractAbility ability) {
        this.customFallingBlock = customFallingBlock;
        this.owner = owner;
        this.ability = ability;
    }

    public FallingBlock getFallingBlock() {
        return customFallingBlock;
    }

    public void setFallingBlock(FallingBlock fallingBlock) {
        this.customFallingBlock = fallingBlock;
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
