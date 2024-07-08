package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.CalculateSpeed;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsAddSpeedModifierEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final CalculateSpeed.Modifier modifier;
    private boolean enhanced = false;
    private boolean cancelled = false;

    public WarlordsAddSpeedModifierEvent(@Nonnull WarlordsEntity player, CalculateSpeed.Modifier modifier) {
        super(player);
        this.modifier = modifier;
    }

    public CalculateSpeed.Modifier getModifier() {
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