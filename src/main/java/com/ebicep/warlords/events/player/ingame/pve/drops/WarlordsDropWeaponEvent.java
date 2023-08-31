package com.ebicep.warlords.events.player.ingame.pve.drops;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.HandlerList;

public class WarlordsDropWeaponEvent extends AbstractWarlordsDropRewardEvent {
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public WarlordsDropWeaponEvent(
            WarlordsEntity player,
            AbstractMob<?> deadMob,
            AtomicDouble dropRate
    ) {
        super(player, deadMob, RewardType.WEAPON, dropRate);
    }

}
