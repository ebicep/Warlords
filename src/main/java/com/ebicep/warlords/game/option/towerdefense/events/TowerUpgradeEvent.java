package com.ebicep.warlords.game.option.towerdefense.events;

import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;
import com.ebicep.warlords.player.ingame.WarlordsEntity;

public class TowerUpgradeEvent<T extends AbstractTower & Upgradeable> extends AbstractTowerEvent<T> {

    private final WarlordsEntity warlordsEntity;
    private final TowerUpgrade towerUpgrade;
    private final boolean sneakUpgraded;

    public TowerUpgradeEvent(T tower, WarlordsEntity warlordsEntity, TowerUpgrade towerUpgrade, boolean sneakUpgraded) {
        super(tower);
        this.warlordsEntity = warlordsEntity;
        this.towerUpgrade = towerUpgrade;
        this.sneakUpgraded = sneakUpgraded;
    }

    public WarlordsEntity getWarlordsEntity() {
        return warlordsEntity;
    }

    public TowerUpgrade getTowerUpgrade() {
        return towerUpgrade;
    }

    public boolean isSneakUpgraded() {
        return sneakUpgraded;
    }
}
