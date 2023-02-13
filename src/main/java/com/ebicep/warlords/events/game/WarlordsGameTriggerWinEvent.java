package com.ebicep.warlords.events.game;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WarlordsGameTriggerWinEvent extends AbstractWarlordsGameEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    @Nonnull
    private final Option cause;
    @Nullable
    private Team declaredWinner;
    private boolean cancelled;

    public WarlordsGameTriggerWinEvent(@Nonnull Game game, @Nonnull Option cause, @Nullable Team declaredWinner) {
        super(game);
        this.cause = cause;
        this.declaredWinner = declaredWinner;
    }

    @Nonnull
    public Option getCause() {
        return cause;
    }

    public @org.jetbrains.annotations.Nullable Team getDeclaredWinner() {
        return declaredWinner;
    }

    public void setDeclaredWinner(@org.jetbrains.annotations.Nullable Team declaredWinner) {
        this.declaredWinner = declaredWinner;
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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