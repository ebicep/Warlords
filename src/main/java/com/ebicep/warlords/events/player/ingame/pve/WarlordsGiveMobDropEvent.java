package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobDrops;
import org.bukkit.event.HandlerList;

public class WarlordsGiveMobDropEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();
    private final MobDrops mobDrop;

    public WarlordsGiveMobDropEvent(WarlordsEntity player, MobDrops mobDrop) {
        super(player);
        this.mobDrop = mobDrop;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public MobDrops getMobDrop() {
        return mobDrop;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
