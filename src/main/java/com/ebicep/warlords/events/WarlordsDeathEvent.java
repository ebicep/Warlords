package com.ebicep.warlords.events;

import com.ebicep.warlords.player.ingame.AbstractWarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WarlordsDeathEvent extends AbstractWarlordsPlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    @Nullable
    private final AbstractWarlordsEntity killer;

    public WarlordsDeathEvent(@Nonnull AbstractWarlordsEntity player, @Nullable AbstractWarlordsEntity killer) {
        super(player);
        this.killer = killer;
        if (killer != null && player.getGame() != killer.getGame()) {
            throw new IllegalArgumentException("Victim and killer not in the same game!");
        }
    }

    @Nullable
    public AbstractWarlordsEntity getKiller() {
        return killer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
