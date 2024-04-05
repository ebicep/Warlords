package com.ebicep.warlords.game.option.towerdefense.events;

import com.ebicep.warlords.events.game.AbstractWarlordsGameEvent;
import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public abstract class AbstractTowerEvent<T extends AbstractTower> extends AbstractWarlordsGameEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final T tower;

    public AbstractTowerEvent(T tower) {
        super(tower.getGame());
        this.tower = tower;
    }

    public T getTower() {
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
