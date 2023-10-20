package com.ebicep.warlords.events.player.ingame.pve.drops;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.HandlerList;

public class WarlordsDropItemEvent extends AbstractWarlordsDropRewardEvent {


    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final ItemTier itemTier;

    public WarlordsDropItemEvent(
            WarlordsEntity player,
            AbstractMob deadMob,
            AtomicDouble dropRate,
            ItemTier itemTier
    ) {
        super(player, deadMob, RewardType.ITEM, dropRate);
        this.itemTier = itemTier;
    }

    public ItemTier getItemTier() {
        return itemTier;
    }

}
