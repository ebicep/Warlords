package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public abstract class WarlordsEnergyUseEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private final String from;
    private final float energyUsed;
    private boolean cancelled;

    public WarlordsEnergyUseEvent(@Nonnull WarlordsEntity player, String from, float energyUsed) {
        super(player);
        this.from = from;
        this.energyUsed = energyUsed;
    }

    public String getFrom() {
        return from;
    }

    public float getEnergyUsed() {
        return energyUsed;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static class Pre extends WarlordsEnergyUseEvent {

        private static final HandlerList handlers = new HandlerList();

        public static HandlerList getHandlerList() {
            return handlers;
        }

        public Pre(@Nonnull WarlordsEntity player, String from, float energyUsed) {
            super(player, from, energyUsed);
        }

        @Nonnull
        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

    }

    public static class Post extends WarlordsEnergyUseEvent {

        private static final HandlerList handlers = new HandlerList();

        public static HandlerList getHandlerList() {
            return handlers;
        }

        public Post(@Nonnull WarlordsEntity player, String from, float energyUsed) {
            super(player, from, energyUsed);
        }

        @Nonnull
        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

    }


}
