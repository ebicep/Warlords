package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.warlords.pve.mobs.Mob;

public enum TowerDefenseMobInfo {

    ZOMBIE_I(Mob.ZOMBIE_I, 100, 100, 0),
    ZOMBIE_II(Mob.ZOMBIE_II, 100, 100, 0),
    SKELETON_I(Mob.SKELETON_I, 100, 100, 0),

    ;

    public static final TowerDefenseMobInfo[] ZOMBIE = {ZOMBIE_I, ZOMBIE_II};
    public static final TowerDefenseMobInfo[] SKELETON = {SKELETON_I};

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
