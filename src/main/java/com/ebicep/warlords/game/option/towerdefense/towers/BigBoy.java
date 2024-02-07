package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.game.Game;
import org.bukkit.Location;

public class BigBoy extends AbstractTower {


    public BigBoy(Game game, Location location) {
        super(game, location);
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.BIG_BOY;
    }

}
