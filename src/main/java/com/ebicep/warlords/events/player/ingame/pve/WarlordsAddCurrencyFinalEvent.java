package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

public class WarlordsAddCurrencyFinalEvent extends AbstractWarlordsEntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public WarlordsAddCurrencyFinalEvent(WarlordsEntity player) {
        super(player);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
