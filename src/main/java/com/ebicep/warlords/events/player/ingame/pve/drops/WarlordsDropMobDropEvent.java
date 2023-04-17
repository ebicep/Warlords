package com.ebicep.warlords.events.player.ingame.pve.drops;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.HandlerList;

public class WarlordsDropMobDropEvent extends AbstractWarlordsDropRewardEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final MobDrops mobDrop;

    public WarlordsDropMobDropEvent(
            WarlordsEntity player,
            AbstractMob<?> deadMob,
            AtomicDouble dropRate,
            MobDrops mobDrop
    ) {
        super(player, deadMob, RewardType.MOB_DROP, dropRate);
        this.mobDrop = mobDrop;
    }

    public MobDrops getMobDrop() {
        return mobDrop;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
