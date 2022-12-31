package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class WarlordsAddSpeedModifierEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final WarlordsEntity from;
    private final AtomicReference<String> name;
    private final AtomicInteger modifier;
    private final AtomicInteger duration;
    private final AtomicReference<String[]> toDisable;
    private boolean enhanced = false;

    public WarlordsAddSpeedModifierEvent(
            WarlordsEntity warlordsEntity,
            WarlordsEntity from, AtomicReference<String> name,
            AtomicInteger modifier,
            AtomicInteger duration,
            AtomicReference<String[]> toDisable
    ) {
        super(warlordsEntity);
        this.from = from;
        this.name = name;
        this.modifier = modifier;
        this.duration = duration;
        this.toDisable = toDisable;
    }

    public WarlordsEntity getFrom() {
        return from;
    }

    public AtomicReference<String> getName() {
        return name;
    }

    public AtomicInteger getModifier() {
        return modifier;
    }

    public AtomicInteger getDuration() {
        return duration;
    }

    public AtomicReference<String[]> getToDisable() {
        return toDisable;
    }

    public boolean isEnhanced() {
        return enhanced;
    }

    public void setEnhanced(boolean enhanced) {
        this.enhanced = enhanced;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}