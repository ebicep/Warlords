package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.title.Title;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class WarlordsDeathEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Nullable
    private final WarlordsEntity killer;
    private final DeathInfo deathInfo;
    private boolean cancelled = false;
    private boolean forceCancel = false; // bypasses deathInfo forced

    public WarlordsDeathEvent(@NotNull WarlordsEntity player, @Nullable WarlordsEntity killer, DeathInfo deathInfo) {
        super(player);
        this.killer = killer;
        this.deathInfo = deathInfo;
        if (killer != null && player.getGame() != killer.getGame()) {
            throw new IllegalArgumentException("Victim and killer not in the same game!");
        }
    }

    @NotNull
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

    public boolean isForceCancel() {
        return forceCancel;
    }

    public void setForceCancel(boolean forceCancel) {
        this.forceCancel = forceCancel;
    }

    @Nullable
    public WarlordsEntity getKiller() {
        return killer;
    }

    public record DeathInfo(@Nullable Title title, @Nullable Runnable onDeathRunnable, boolean forced) {

    }

    public static class DeathInfoBuilder {

        public static DeathInfoBuilder create() {
            return new DeathInfoBuilder();
        }

        private Title title = null;
        private Runnable onDeathRunnable = null;
        private boolean forced = false;

        public DeathInfoBuilder setTitle(Title title) {
            this.title = title;
            return this;
        }

        public DeathInfoBuilder setOnDeathRunnable(Runnable onDeathRunnable) {
            this.onDeathRunnable = onDeathRunnable;
            return this;
        }

        public DeathInfoBuilder setForced(boolean forced) {
            this.forced = forced;
            return this;
        }

        public WarlordsDeathEvent.DeathInfo createDeathInfo() {
            return new DeathInfo(title, onDeathRunnable, forced);
        }

    }

}
