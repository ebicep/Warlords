package com.ebicep.customentities.nms;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.player.WarlordsEntity;
import org.bukkit.entity.FallingBlock;

public class CustomFallingBlock {
    private FallingBlock customFallingBlock;
    private WarlordsEntity owner;
    private AbstractAbility ability;
    private int ticksLived = 0;

    public CustomFallingBlock(FallingBlock customFallingBlock, WarlordsEntity owner, AbstractAbility ability) {
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

    public WarlordsEntity getOwner() {
        return owner;
    }

    public void setOwner(WarlordsEntity owner) {
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
