package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.game.Game;
import org.bukkit.Location;

import java.util.UUID;

public class BigBoy extends AbstractTower {


    public BigBoy(Game game, UUID owner, Location location) {
        super(game, owner, location);
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.BIG_BOY;
    }

}
