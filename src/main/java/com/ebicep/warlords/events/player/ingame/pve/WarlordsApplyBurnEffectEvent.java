package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsApplyBurnEffectEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();
    private final WarlordsEntity from;
    private int tickPeriod;


    public WarlordsApplyBurnEffectEvent(@Nonnull WarlordsEntity player, WarlordsEntity from, int tickPeriod) {
        super(player);
        this.from = from;
        this.tickPeriod = tickPeriod;
    }

    public void setTickPeriod(int tickPeriod) {
        this.tickPeriod = tickPeriod;
    }

    public int getTickPeriod() {
        return tickPeriod;
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
