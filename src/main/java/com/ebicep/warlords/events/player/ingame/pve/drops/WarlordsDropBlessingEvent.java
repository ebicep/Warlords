package com.ebicep.warlords.events.player.ingame.pve.drops;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.HandlerList;

public class WarlordsDropBlessingEvent extends AbstractWarlordsDropRewardEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public WarlordsDropBlessingEvent(
            WarlordsEntity player,
            AbstractMob<?> deadMob,
            AtomicDouble dropRate
    ) {
        super(player, deadMob, RewardType.BLESSING, dropRate);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
