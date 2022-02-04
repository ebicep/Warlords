package com.ebicep.warlords.events;

import com.ebicep.warlords.maps.option.IntersectionPointOption;
import org.bukkit.event.HandlerList;


public class WarlordsIntersectionCaptureEvent extends WarlordsGameEvent {
    private static final HandlerList handlers = new HandlerList();

	private final IntersectionPointOption option;

	public WarlordsIntersectionCaptureEvent(IntersectionPointOption option) {
		super(option.getGame());
		this.option = option;
	}

	public IntersectionPointOption getOption() {
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
