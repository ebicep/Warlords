package com.ebicep.warlords.events;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.flags.FlagInfo;
import com.ebicep.warlords.maps.flags.FlagLocation;
import com.ebicep.warlords.maps.flags.FlagManager;
import com.ebicep.warlords.maps.state.PlayingState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WarlordsFlagUpdatedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Game game;
    private final PlayingState gameState;
    private final FlagInfo info;
    private final Team team;
    private final FlagLocation old;

    public WarlordsFlagUpdatedEvent(Game game, PlayingState gameState, FlagInfo info, Team team, FlagLocation old) {
        this.game = game;
        this.gameState = gameState;
        this.info = info;
        this.team = team;
        this.old = old;
    }

    public Game getGame() {
        return game;
    }

    public PlayingState getGameState() {
        return gameState;
    }

    public FlagInfo getInfo() {
        return info;
    }

    public Team getTeam() {
        return team;
    }

    public FlagLocation getOld() {
        return old;
    }

    public FlagLocation getNew() {
        return info.getFlag();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public String toString() {
        return "WarlordsFlagUpdatedEvent{" + "game=" + game + ", gameState=" + gameState + ", info=" + info + ", team=" + team + ", old=" + old + '}';
    }
}