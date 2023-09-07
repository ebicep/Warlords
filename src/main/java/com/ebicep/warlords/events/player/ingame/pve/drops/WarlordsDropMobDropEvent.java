package com.ebicep.warlords.events.player.ingame.pve.drops;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.HandlerList;

public class WarlordsDropMobDropEvent extends AbstractWarlordsDropRewardEvent {

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final MobDrop mobDrop;

    public WarlordsDropMobDropEvent(
            WarlordsEntity player,
            AbstractMob<?> deadMob,
            AtomicDouble dropRate,
            MobDrop mobDrop
    ) {
        super(player, deadMob, RewardType.MOB_DROP, dropRate);
        this.mobDrop = mobDrop;
    }

    public MobDrop getMobDrop() {
        return mobDrop;
    }

}
