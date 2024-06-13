package com.ebicep.warlords.game.option.towerdefense.towers.trooptype;

import com.ebicep.warlords.game.option.towerdefense.mobs.TowerDefenseMob;

public class AirTroop implements TDTroopType {

    @Override
    public boolean canAttack(TowerDefenseMob mob) {
        return true;
    }

}
