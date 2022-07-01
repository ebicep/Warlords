package com.ebicep.warlords.events;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WarlordsDeathEvent extends AbstractWarlordsPlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    @Nullable
    private final WarlordsEntity killer;

    public WarlordsDeathEvent(@Nonnull WarlordsEntity player, @Nullable WarlordsEntity killer) {
        super(player);
        this.killer = killer;
        if (killer != null && player.getGame() != killer.getGame()) {
            throw new IllegalArgumentException("Victim and killer not in the same game!");
        }
    }

    @Nullable
    public WarlordsEntity getKiller() {
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
