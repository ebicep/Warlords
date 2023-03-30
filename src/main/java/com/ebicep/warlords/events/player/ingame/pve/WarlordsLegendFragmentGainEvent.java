package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicLong;

public class WarlordsLegendFragmentGainEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();
    private final AtomicLong legendFragments;
    private final WaveDefenseOption waveDefenseOption;

    public WarlordsLegendFragmentGainEvent(@Nonnull WarlordsEntity player, AtomicLong legendFragments, WaveDefenseOption waveDefenseOption) {
        super(player);
        this.legendFragments = legendFragments;
        this.waveDefenseOption = waveDefenseOption;
    }

    public AtomicLong getLegendFragments() {
        return legendFragments;
    }

    public WaveDefenseOption getWaveDefenseOption() {
        return waveDefenseOption;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
