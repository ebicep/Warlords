package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicLong;

public class WarlordsLegendFragmentGainEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();
    private final AtomicLong legendFragments;
    private final PveOption pveOption;
    private final int per5;

    public WarlordsLegendFragmentGainEvent(@Nonnull WarlordsEntity player, AtomicLong legendFragments, PveOption pveOption, int per5) {
        super(player);
        this.legendFragments = legendFragments;
        this.pveOption = pveOption;
        this.per5 = per5;
    }

    public AtomicLong getLegendFragments() {
        return legendFragments;
    }

    public PveOption getPveOption() {
        return pveOption;
    }

    public int getPer5() {
        return per5;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
