package com.ebicep.warlords.events.player.pve;

import com.ebicep.warlords.events.player.AbstractWarlordsPlayerEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import java.util.concurrent.atomic.AtomicInteger;

public class WarlordsPlayerAddCurrencyEvent extends AbstractWarlordsPlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final AtomicInteger currencyToAdd;

    public WarlordsPlayerAddCurrencyEvent(WarlordsEntity player, AtomicInteger currencyToAdd) {
        super(player);
        this.currencyToAdd = currencyToAdd;
    }

    public AtomicInteger getCurrencyToAdd() {
        return currencyToAdd;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
