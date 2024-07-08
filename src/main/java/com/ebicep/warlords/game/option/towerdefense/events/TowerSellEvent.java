package com.ebicep.warlords.game.option.towerdefense.events;

import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;

public class TowerSellEvent extends AbstractTowerEvent<AbstractTower> {
    public TowerSellEvent(AbstractTower tower) {
        super(tower);
    }
}
