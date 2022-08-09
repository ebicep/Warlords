package com.ebicep.warlords.events.game;

import com.ebicep.warlords.game.Game;
import org.bukkit.event.HandlerList;

public class WarlordsGameWaveClearEvent extends AbstractWarlordsGameEvent {
    private static final HandlerList handlers = new HandlerList();
    private final int waveCleared;

    public WarlordsGameWaveClearEvent(Game game, int waveCleared) {
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
