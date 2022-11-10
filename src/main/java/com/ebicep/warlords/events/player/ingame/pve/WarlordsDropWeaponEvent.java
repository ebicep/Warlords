package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.HandlerList;

public class WarlordsDropWeaponEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();
    private final AtomicDouble dropRate;

    public WarlordsDropWeaponEvent(WarlordsEntity player, AtomicDouble dropRate) {
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
