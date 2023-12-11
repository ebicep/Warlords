package com.ebicep.warlords.events.game.pve;

import com.ebicep.warlords.events.game.AbstractWarlordsGameEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.pve.mobs.bosses.MagmaticOoze;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsMagmaticOozeSplitEvent extends AbstractWarlordsGameEvent {
    private static final HandlerList handlers = new HandlerList();
    private final MagmaticOoze magmaticOoze;

    public WarlordsMagmaticOozeSplitEvent(Game game, MagmaticOoze magmaticOoze) {
        super(game);
        this.magmaticOoze = magmaticOoze;
    }

    public MagmaticOoze getMagmaticOoze() {
        return magmaticOoze;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
