package com.ebicep.warlords.game.option.towerdefense.towers.trooptype;

import com.ebicep.warlords.game.option.towerdefense.mobs.TowerDefenseMob;

public interface TDTroopType {

    GroundTroop DEFAULT = new GroundTroop();

    boolean canAttack(TowerDefenseMob mob);

}

