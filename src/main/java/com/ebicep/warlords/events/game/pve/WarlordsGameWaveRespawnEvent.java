package com.ebicep.warlords.events.game.pve;

import com.ebicep.warlords.events.game.AbstractWarlordsGameEvent;
import com.ebicep.warlords.game.Game;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class WarlordsGameWaveRespawnEvent extends AbstractWarlordsGameEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    protected boolean cancelled = false;

    public WarlordsGameWaveRespawnEvent(Game game) {
        super(game);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
