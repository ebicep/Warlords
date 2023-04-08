package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.HandlerList;

public class WarlordsDropRewardEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();

    private final AbstractMob<?> deadMob;
    private final RewardType rewardType;
    private final AtomicDouble dropRate;
    private double modifier = 1;

    public WarlordsDropRewardEvent(WarlordsEntity player, AbstractMob<?> deadMob, RewardType rewardType, AtomicDouble dropRate) {
        super(player);
        this.deadMob = deadMob;
        this.rewardType = rewardType;
        this.dropRate = dropRate;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public AbstractMob<?> getDeadMob() {
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static enum RewardType {
        WEAPON,
        MOB_DROP,
        ITEM,
        BLESSING,


    }
}
