package com.ebicep.warlords.game.option.towerdefense.towers;

import java.util.function.Supplier;

public enum TowerRegistry {

    PYRO_TOWER(PyroTower::new),
    BIG_BOY(BigBoy::new),

    ;

    public static final TowerRegistry[] VALUES = values();
    public final Supplier<AbstractTower> create;
    public String name;

    TowerRegistry(Supplier<AbstractTower> create) {
        this.create = create;
    }
}
