package com.ebicep.warlords.events.player;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsPlayerEnergyUsed extends AbstractWarlordsPlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final float energyUsed;

    public WarlordsPlayerEnergyUsed(@Nonnull WarlordsEntity player, float energyUsed) {
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
