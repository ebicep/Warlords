package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;

import javax.annotation.Nonnull;

public class WarlordsAddPotionEffectEvent extends AbstractWarlordsEntityEvent {
    private static final HandlerList handlers = new HandlerList();

    private final PotionEffect potionEffect;

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

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
