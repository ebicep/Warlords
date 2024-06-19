package com.ebicep.warlords.events.player.ingame.pve.drops;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public abstract class AbstractWarlordsDropRewardEvent extends AbstractWarlordsEntityEvent {

    protected static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final AbstractMob deadMob;
    private final RewardType rewardType;
    private final AtomicDouble dropRate;
    private double modifier = 1;

    public AbstractWarlordsDropRewardEvent(WarlordsEntity player, AbstractMob deadMob, RewardType rewardType, AtomicDouble dropRate) {
        super(player);
        this.deadMob = deadMob;
        this.rewardType = rewardType;
        this.dropRate = dropRate;
    }

    public AbstractMob getDeadMob() {
        return deadMob;
    }

    public RewardType getRewardType() {
        return rewardType;
    }

    public AtomicDouble getDropRate() {
        return dropRate;
    }

    public double getModifier() {
        return modifier;
    }

    public void setModifier(double modifier) {
        this.modifier = modifier;
    }

    public void addModifier(double modifier) {
        this.modifier += modifier;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public enum RewardType {
        WEAPON,
        MOB_DROP,
        ITEM,
        BLESSING,
    }
}
