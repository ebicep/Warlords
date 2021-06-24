package com.ebicep.warlords.events;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import javax.annotation.Nonnull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WarlordsPointsChangedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Game game;
    private final Team team;
    private final int oldPoints;
    private final int newPoints;

    public WarlordsPointsChangedEvent(@Nonnull Game game, @Nonnull Team team, int oldPoints, int newPoints) {
        this.game = game;
        this.team = team;
        this.oldPoints = oldPoints;
        this.newPoints = newPoints;
    }

    @Nonnull
    public Game getGame() {
        return game;
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
