package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsPlayerEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.HandlerList;

public class WarlordsPlayerDropWeaponEvent extends AbstractWarlordsPlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final AtomicDouble dropRate;

    public WarlordsPlayerDropWeaponEvent(WarlordsEntity player, AtomicDouble dropRate) {
        super(player);
        this.dropRate = dropRate;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public AtomicDouble getDropRate() {
        return dropRate;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
