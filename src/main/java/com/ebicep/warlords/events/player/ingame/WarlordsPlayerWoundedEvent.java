package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.abilities.internal.WoundingCooldown;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class WarlordsPlayerWoundedEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final WarlordsEntity from;
    private final String name;
    private float amount;
    private int tickDuration;
    @Nullable
    private WoundingCooldown woundingCooldown; // true if player wasnt wounded before

    private boolean cancelled;

    public WarlordsPlayerWoundedEvent(
            @Nonnull WarlordsEntity player,
            @Nonnull WarlordsEntity from,
            @Nonnull String name,
            float amount,
            int tickDuration,
            @Nullable WoundingCooldown woundingCooldown
    ) {
        super(player);
        this.from = from;
        this.name = name;
        this.amount = amount;
        this.tickDuration = tickDuration;
        this.woundingCooldown = woundingCooldown;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public WarlordsEntity getFrom() {
        return from;
    }

    public String getName() {
        return name;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getTickDuration() {
        return tickDuration;
    }

    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public @Nullable WoundingCooldown getWoundingCooldown() {
        return woundingCooldown;
    }

}
