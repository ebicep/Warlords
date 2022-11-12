package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsEnergyUsedEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();

    private final float energyUsed;

    public WarlordsEnergyUsedEvent(@Nonnull WarlordsEntity player, float energyUsed) {
        super(player);
        this.energyUsed = energyUsed;
    }

    public float getEnergyUsed() {
        return energyUsed;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
