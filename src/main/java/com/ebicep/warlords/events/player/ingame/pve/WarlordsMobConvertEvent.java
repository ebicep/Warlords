package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.List;

public class WarlordsMobConvertEvent extends AbstractWarlordsEntityEvent {
    private static final HandlerList handlers = new HandlerList();
    private final List<WarlordsNPC> converted;

    public WarlordsMobConvertEvent(@Nonnull WarlordsEntity player, List<WarlordsNPC> converted) {
        super(player);
        this.converted = converted;
    }


    public List<WarlordsNPC> getConverted() {
        return converted;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
