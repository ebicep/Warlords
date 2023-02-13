package com.ebicep.warlords.events.game;

import com.ebicep.warlords.game.option.pvp.InterceptionPointOption;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;


public class WarlordsIntersectionCaptureEvent extends AbstractWarlordsGameEvent {
    private static final HandlerList handlers = new HandlerList();

    private final InterceptionPointOption option;

    public WarlordsIntersectionCaptureEvent(InterceptionPointOption option) {
        super(option.getGame());
        this.option = option;
    }

    public InterceptionPointOption getOption() {
		return option;
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
