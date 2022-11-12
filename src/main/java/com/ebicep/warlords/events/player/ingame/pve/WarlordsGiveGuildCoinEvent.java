package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.HandlerList;

public class WarlordsGiveGuildCoinEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();
    private final AtomicDouble coinConversionRate;

    public WarlordsGiveGuildCoinEvent(WarlordsEntity warlordsEntity, AtomicDouble coinConversionRate) {
        super(warlordsEntity);
        this.coinConversionRate = coinConversionRate;
    }

    public AtomicDouble getCoinConversionRate() {
        return coinConversionRate;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
