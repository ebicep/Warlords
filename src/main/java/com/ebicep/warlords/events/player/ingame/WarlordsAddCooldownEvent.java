package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsAddCooldownEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final AbstractCooldown<?> abstractCooldown;
    private boolean cancelled;

    public WarlordsAddCooldownEvent(@Nonnull WarlordsEntity player, AbstractCooldown<?> abstractCooldown) {
        super(player);
        this.abstractCooldown = abstractCooldown;
    }


    public AbstractCooldown<?> getAbstractCooldown() {
        return abstractCooldown;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
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