package com.ebicep.warlords.events.game.pve;

import com.ebicep.warlords.events.game.AbstractWarlordsGameEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import org.bukkit.event.HandlerList;

public class WarlordsMobSpawnEvent extends AbstractWarlordsGameEvent {
    private static final HandlerList handlers = new HandlerList();
    private final AbstractMob<?> mob;

    public WarlordsMobSpawnEvent(Game game, AbstractMob<?> mob) {
        super(game);
        this.mob = mob;
    }

    public AbstractMob<?> getMob() {
        return mob;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
