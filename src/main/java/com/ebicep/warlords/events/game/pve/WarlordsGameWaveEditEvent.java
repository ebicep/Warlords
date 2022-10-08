package com.ebicep.warlords.events.game.pve;

import com.ebicep.warlords.events.game.AbstractWarlordsGameEvent;
import com.ebicep.warlords.game.Game;
import org.bukkit.event.HandlerList;

public class WarlordsGameWaveEditEvent extends AbstractWarlordsGameEvent {
    private static final HandlerList handlers = new HandlerList();
    private final int waveCleared;

    public WarlordsGameWaveEditEvent(Game game, int waveCleared) {
        super(game);
        this.waveCleared = waveCleared;
    }

    public int getWaveCleared() {
        return waveCleared;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
