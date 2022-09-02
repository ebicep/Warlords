package com.ebicep.warlords.events.player.pve;

import com.ebicep.warlords.events.player.AbstractWarlordsPlayerEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import java.util.concurrent.atomic.AtomicInteger;

public class WarlordsPlayerGiveRespawnEvent extends AbstractWarlordsPlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final AtomicInteger respawnTimer;

    public WarlordsPlayerGiveRespawnEvent(WarlordsEntity player, AtomicInteger respawnTimer) {
        super(player);
        this.respawnTimer = respawnTimer;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public AtomicInteger getRespawnTimer() {
        return respawnTimer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
