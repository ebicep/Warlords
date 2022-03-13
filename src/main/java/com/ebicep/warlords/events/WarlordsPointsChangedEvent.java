package com.ebicep.warlords.events;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;

import java.util.Objects;

import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Thrown when the points of the game changes
 */
public class WarlordsPointsChangedEvent extends AbstractWarlordsGameEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Team team;
    private final int oldPoints;
    private final int newPoints;

    public WarlordsPointsChangedEvent(@Nonnull Game game, @Nonnull Team team, int oldPoints, int newPoints) {
        super(game);
        this.team = Objects.requireNonNull(team, "team");
        this.oldPoints = oldPoints;
        this.newPoints = newPoints;
    }

    @Nonnull
    public Team getTeam() {
        return team;
    }

    public int getOldPoints() {
        return oldPoints;
    }

    public int getNewPoints() {
        return newPoints;
    }

    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
