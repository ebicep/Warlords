package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import java.util.LinkedHashMap;

public class WarlordsCoinSummaryEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();
    private final LinkedHashMap<String, Long> coinSummary;

    public WarlordsCoinSummaryEvent(WarlordsEntity player, LinkedHashMap<String, Long> coinSummary) {
        super(player);
        this.coinSummary = coinSummary;
    }

    public LinkedHashMap<String, Long> getCurrencyToAdd() {
        return coinSummary;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
