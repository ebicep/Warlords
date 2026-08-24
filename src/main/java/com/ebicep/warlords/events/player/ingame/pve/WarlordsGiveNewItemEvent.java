package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.newitems.NewItem;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsGiveNewItemEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();
    private final NewItem item;

    public WarlordsGiveNewItemEvent(WarlordsEntity player, NewItem item) {
        super(player);
        this.item = item;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public NewItem getItem() {
        return item;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
