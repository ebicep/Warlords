package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.warlords.pve.mobs.Mob;

public enum TowerDefenseMobInfo {

    ZOMBIE(Mob.TD_ZOMBIE, 100, 100, 10),
    SKELETON(Mob.TD_SKELETON, 100, 100, 10),

    ;

    private final Mob mob;
    private final int cost; // how much it costs to spawn
    private final int incomeModifier; // how it affects the income rate
    private final int spawnDelay; // ticks

    TowerDefenseMobInfo(Mob mob, int cost, int incomeModifier, int spawnDelay) {
        this.mob = mob;
        this.cost = cost;
        this.incomeModifier = incomeModifier;
        this.spawnDelay = spawnDelay;
    }

    public Mob getMob() {
        return mob;
    }

    public int getCost() {
        return cost;
    }

    public int getIncomeModifier() {
        return incomeModifier;
    }

    public int getSpawnDelay() {
        return spawnDelay;
    }
}
