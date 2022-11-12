package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsAddCooldownEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();

    private final AbstractCooldown<?> abstractCooldown;

    public WarlordsAddCooldownEvent(@Nonnull WarlordsEntity player, AbstractCooldown<?> abstractCooldown) {
        super(player);
        this.abstractCooldown = abstractCooldown;
    }


    public AbstractCooldown<?> getAbstractCooldown() {
        return abstractCooldown;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}