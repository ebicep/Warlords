package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.MobDrops;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class WarlordsGiveMobDropEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();
    private final MobDrops mobDrop;
    private final List<WarlordsPlayer> stolenBy = new ArrayList<>();

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

    public List<WarlordsPlayer> getStolenBy() {
        return stolenBy;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
