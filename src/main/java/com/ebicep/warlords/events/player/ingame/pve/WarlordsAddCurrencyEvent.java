package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WarlordsAddCurrencyEvent extends AbstractWarlordsEntityEvent {
    private static final HandlerList handlers = new HandlerList();
    private float currencyToAdd;
    @Nullable
    private final WarlordsDeathEvent killSource;

    public WarlordsAddCurrencyEvent(WarlordsEntity player, float currencyToAdd) {
        this(player, currencyToAdd, null);
    }

    public WarlordsAddCurrencyEvent(WarlordsEntity player, float currencyToAdd, @Nullable WarlordsDeathEvent killSource) {
        super(player);
        this.currencyToAdd = currencyToAdd;
        this.killSource = killSource;
    }

    /**
     * @return the death that awarded this currency, or null if it did not come from a kill
     */
    @Nullable
    public WarlordsDeathEvent getKillSource() {
        return killSource;
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
