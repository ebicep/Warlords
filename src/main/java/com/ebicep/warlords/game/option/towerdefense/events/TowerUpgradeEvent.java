package com.ebicep.warlords.game.option.towerdefense.events;

import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;

public class TowerUpgradeEvent extends AbstractTowerEvent {
    public TowerUpgradeEvent(AbstractTower tower) {
        super(tower);
    }
}
