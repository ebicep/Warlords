package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsPlayerEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import java.util.LinkedHashMap;

public class WarlordsPlayerCoinSummaryEvent extends AbstractWarlordsPlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final LinkedHashMap<String, Long> coinSummary;

    public WarlordsPlayerCoinSummaryEvent(WarlordsEntity player, LinkedHashMap<String, Long> coinSummary) {
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
