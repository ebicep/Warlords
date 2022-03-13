package com.ebicep.warlords.events;

import com.ebicep.warlords.game.option.InterceptionPointOption;
import org.bukkit.event.HandlerList;


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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
