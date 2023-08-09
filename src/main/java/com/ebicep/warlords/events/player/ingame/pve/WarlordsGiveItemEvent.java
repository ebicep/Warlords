package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsGiveItemEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();
    private final AbstractItem item;

    public WarlordsGiveItemEvent(WarlordsEntity player, AbstractItem item) {
        super(player);
        this.item = item;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public AbstractItem getItem() {
        return item;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
