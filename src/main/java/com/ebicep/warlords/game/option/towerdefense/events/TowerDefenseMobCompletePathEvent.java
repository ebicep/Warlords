package com.ebicep.warlords.game.option.towerdefense.events;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.mobs.TowerDefenseMob;

public class TowerDefenseMobCompletePathEvent extends AbstractTowerDefenseMobEvent {


    public TowerDefenseMobCompletePathEvent(Game game, TowerDefenseMob mob) {
        super(game, mob);
    }
}
