package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;

import javax.annotation.Nonnull;

public class WarlordsAddPotionEffectEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final PotionEffect potionEffect;
    private boolean cancelled = false;

    public WarlordsAddPotionEffectEvent(@Nonnull WarlordsEntity player, PotionEffect potionEffect) {
        super(player);
        this.potionEffect = potionEffect;
    }

    public PotionEffect getPotionEffect() {
        return potionEffect;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}