package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.game.option.towerdefense.TowerCache;
import org.bukkit.Location;

public class PyroTower implements Tower {

    @Override
    public void build(Location frontLeftCorner) {
        Tower.build(frontLeftCorner, TowerCache.Tower.PYRO_TOWER_1.data);
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return null;
    }

}
