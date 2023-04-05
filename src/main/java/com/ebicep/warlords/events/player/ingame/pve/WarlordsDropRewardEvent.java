package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.HandlerList;

public class WarlordsDropRewardEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();

    private final RewardType rewardType;
    private final AtomicDouble dropRate;

    public WarlordsDropRewardEvent(WarlordsEntity player, RewardType rewardType, AtomicDouble dropRate) {
        super(player);
        this.rewardType = rewardType;
        this.dropRate = dropRate;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public RewardType getRewardType() {
        return rewardType;
    }

    public AtomicDouble getDropRate() {
        return dropRate;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static enum RewardType {
        WEAPON,
        MOB_DROP,
        ITEM
    }
}
