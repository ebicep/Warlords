package com.ebicep.warlords.events.game;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsScoreOnEventEvent<T> extends AbstractWarlordsGameEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final T event;
    private final Team team;
    private final int score;
    private boolean cancelled = false;

    public WarlordsScoreOnEventEvent(Game game, T event, Team team, int score) {
        super(game);
        this.event = event;
        this.team = team;
        this.score = score;
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

    public T getEvent() {
        return event;
    }

    public Team getTeam() {
        return team;
    }

    public int getScore() {
        return score;
    }


}
