package com.ebicep.warlords.game.option.towerdefense.events;

import com.ebicep.warlords.events.game.AbstractWarlordsGameEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseCastle;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class TowerDefenseCastleDestroyEvent extends AbstractWarlordsGameEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final TowerDefenseCastle castle;

    public TowerDefenseCastleDestroyEvent(Game game, TowerDefenseCastle castle) {
        super(game);
        this.castle = castle;
    }

    public TowerDefenseCastle getCastle() {
        return castle;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
