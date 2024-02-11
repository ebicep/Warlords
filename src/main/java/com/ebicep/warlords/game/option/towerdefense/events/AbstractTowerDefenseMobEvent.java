package com.ebicep.warlords.game.option.towerdefense.events;

import com.ebicep.warlords.events.game.AbstractWarlordsGameEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.mobs.TowerDefenseMob;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public abstract class AbstractTowerDefenseMobEvent extends AbstractWarlordsGameEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final TowerDefenseMob mob;

    public AbstractTowerDefenseMobEvent(Game game, TowerDefenseMob mob) {
        super(game);
        this.mob = mob;
    }


    public TowerDefenseMob getMob() {
        return mob;
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
