package com.ebicep.warlords.game.option.towerdefense.towers.trooptype;

import com.ebicep.warlords.game.option.towerdefense.mobs.TowerDefenseMob;
import com.ebicep.warlords.game.option.towerdefense.mobs.attributes.type.GroundType;

public class GroundTroop implements TDTroopType {

    @Override
    public boolean canAttack(TowerDefenseMob mob) {
        return mob.getMobType() == GroundType.DEFAULT;
    }

}
