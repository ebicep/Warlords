package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.warlords.pve.mobs.Mob;

public enum TowerDefenseMobInfo {

    ZOMBIE(Mob.TD_ZOMBIE, 100, 100, 0),
    SKELETON(Mob.TD_SKELETON, 100, 100, 0),

    ;

    private final Mob mob;
    private final int cost;
    private final int expReward;
    private final int unlockCost; // exp

    TowerDefenseMobInfo(Mob mob, int cost, int expReward, int unlockCost) {
        this.mob = mob;
        this.cost = cost;
        this.expReward = expReward;
        this.unlockCost = unlockCost;
    }

    public Mob getMob() {
        return mob;
    }

    public int getCost() {
        return cost;
    }

    public int getExpReward() {
        return expReward;
    }

    public int getUnlockCost() {
        return unlockCost;
    }
}
