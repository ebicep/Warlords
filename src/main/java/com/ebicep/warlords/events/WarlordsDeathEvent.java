package com.ebicep.warlords.events;

import com.ebicep.warlords.player.WarlordsPlayer;
import javax.annotation.Nonnull;
import org.bukkit.event.HandlerList;

public class WarlordsDeathEvent extends WarlordsGameEvent {

    private static final HandlerList handlers = new HandlerList();
    private final WarlordsPlayer player;

    public WarlordsDeathEvent(@Nonnull WarlordsPlayer player) {
        super(player.getGame());
        this.player = player;
    }

    public WarlordsPlayer getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}