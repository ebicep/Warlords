package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobDrop;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsGiveStolenMobDropEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();
    private final MobDrop mobDrop;

    public WarlordsGiveStolenMobDropEvent(WarlordsEntity player, MobDrop mobDrop) {
        super(player);
        this.mobDrop = mobDrop;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public MobDrop getMobDrop() {
        return mobDrop;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
