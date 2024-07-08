package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsAddCurrencyEvent extends AbstractWarlordsEntityEvent {
    private static final HandlerList handlers = new HandlerList();
    private float currencyToAdd;

    public WarlordsAddCurrencyEvent(WarlordsEntity player, float currencyToAdd) {
        super(player);
        this.currencyToAdd = currencyToAdd;
    }

    public float getCurrencyToAdd() {
        return currencyToAdd;
    }

    public void setCurrencyToAdd(float currencyToAdd) {
        this.currencyToAdd = currencyToAdd;
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
