package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifier;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsAddSpeedModifierEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final MotionModifier modifier;
    private boolean enhanced = false;
    private boolean cancelled = false;

    public WarlordsAddSpeedModifierEvent(@Nonnull WarlordsEntity player, MotionModifier modifier) {
        super(player);
        this.modifier = modifier;
    }

    public MotionModifier getModifier() {
        return modifier;
    }

    public boolean isEnhanced() {
        return enhanced;
    }

    public void setEnhanced(boolean enhanced) {
        this.enhanced = enhanced;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
