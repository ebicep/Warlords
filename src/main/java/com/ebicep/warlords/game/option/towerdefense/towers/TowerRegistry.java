package com.ebicep.warlords.game.option.towerdefense.towers;

import java.util.function.Supplier;

public enum TowerRegistry {

    PYRO_TOWER(PyroTower::new),
    BIG_BOY(BigBoy::new),

    ;

    public static final TowerRegistry[] VALUES = values();
    public final Supplier<Tower> create;

    TowerRegistry(Supplier<Tower> create) {
        this.create = create;
    }
}
