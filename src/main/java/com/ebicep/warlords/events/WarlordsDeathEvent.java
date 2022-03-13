package com.ebicep.warlords.events;

import com.ebicep.warlords.player.WarlordsPlayer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.event.HandlerList;

public class WarlordsDeathEvent extends AbstractWarlordsPlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    @Nullable
    private final WarlordsPlayer killer;

    public WarlordsDeathEvent(@Nonnull WarlordsPlayer player, @Nullable WarlordsPlayer killer) {
        super(player);
        this.killer = killer;
        if (killer != null && player.getGame() != killer.getGame()) {
            throw new IllegalArgumentException("Victim and killer not in the same game!");
        }
    }

    @Nullable
    public WarlordsPlayer getKiller() {
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
