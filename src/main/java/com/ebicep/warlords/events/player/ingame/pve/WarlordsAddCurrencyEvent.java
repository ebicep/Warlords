package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

public class WarlordsAddCurrencyEvent extends AbstractWarlordsEntityEvent {
    private static final HandlerList handlers = new HandlerList();
    private final AtomicInteger currencyToAdd;
    private boolean modifiable = true;

    public WarlordsAddCurrencyEvent(WarlordsEntity player, AtomicInteger currencyToAdd) {
        super(player);
        this.currencyToAdd = currencyToAdd;
    }

    public AtomicInteger getCurrencyToAdd() {
        return currencyToAdd;
    }

    public boolean isModifiable() {
        return modifiable;
    }

    public void setModifiable(boolean modifiable) {
        this.modifiable = modifiable;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
