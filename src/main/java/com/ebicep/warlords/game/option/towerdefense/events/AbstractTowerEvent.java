package com.ebicep.warlords.game.option.towerdefense.events;

import com.ebicep.warlords.events.game.AbstractWarlordsGameEvent;
import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public abstract class AbstractTowerEvent extends AbstractWarlordsGameEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final AbstractTower tower;

    public AbstractTowerEvent(AbstractTower tower) {
        super(tower.getGame());
        this.tower = tower;
    }

    public AbstractTower getTower() {
        return tower;
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
