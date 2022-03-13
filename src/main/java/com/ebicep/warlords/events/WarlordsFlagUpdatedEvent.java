package com.ebicep.warlords.events;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.flags.FlagInfo;
import com.ebicep.warlords.game.flags.FlagLocation;
import org.bukkit.event.HandlerList;

public class WarlordsFlagUpdatedEvent extends AbstractWarlordsGameEvent {

    private static final HandlerList handlers = new HandlerList();
    private final FlagInfo info;
    private final Team team;
    private final FlagLocation old;

    public WarlordsFlagUpdatedEvent(Game game, FlagInfo info, FlagLocation old) {
        super(game);
        this.info = info;
        this.team = info.getTeam();
        this.old = old;
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
        return "WarlordsFlagUpdatedEvent{" + "game=" + game + ", info=" + info + ", team=" + team + ", old=" + old + '}';
    }
}