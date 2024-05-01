package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.warlords.pve.mobs.Mob;

public enum TowerDefenseMobInfo {

    ZOMBIE(Mob.TD_ZOMBIE, 15, 1, 10),
    ZOMBIE_BABY(Mob.TD_ZOMBIE_BABY, 20, 1, 10),
    ZOMBIE_VILLAGER(Mob.TD_ZOMBIE_VILLAGER, 30, 1.3f, 10),
    HUSK(Mob.TD_HUSK, 0, 0, 10),
    SKELETON(Mob.TD_SKELETON, 50, 1.6f, 10),
    STRAY(Mob.TD_STRAY, 0, 0, 10),
    SPIDER(Mob.TD_SPIDER, 45, 1.6f, 10),
    CAVE_SPIDER(Mob.TD_CAVE_SPIDER, 55, 1.6f, 10),
    SILVERFISH(Mob.TD_SILVERFISH, 25, 1.3f, 10),
    ENDERMITE(Mob.TD_ENDERMITE, 0, 1.9f, 10),
    WITCH(Mob.TD_WITCH, 0, 0, 10),
    ENDERMAN(Mob.TD_ENDERMAN, 0, 0, 10),
    WITHER_SKELETON(Mob.TD_WITHER_SKELETON, 0, 0, 10),
    GHAST(Mob.TD_GHAST, 0, 0, 10),
    BLAZE(Mob.TD_BLAZE, 0, 0, 10),
    PIGLIN(Mob.TD_PIGLIN, 0, 0, 10),
    ZOMBIFIED_PIGLIN(Mob.TD_ZOMBIFIED_PIGLIN, 0, 0, 10),
    PIGLIN_BRUTE(Mob.TD_PIGLIN_BRUTE, 0, 0, 10),
    HOGLIN(Mob.TD_HOGLIN, 0, 0, 10),
    ZOGLIN(Mob.TD_ZOGLIN, 0, 0, 10),
    EVOKER(Mob.TD_EVOKER, 0, 0, 10),
    VINDICATOR(Mob.TD_VINDICATOR, 0, 0, 10),
    PILLAGER(Mob.TD_PILLAGER, 0, 0, 10),
    RAVAGER(Mob.TD_RAVAGER, 0, 0, 10),
    ILLUSIONER(Mob.TD_ILLUSIONER, 0, 0, 10),
    VEX(Mob.TD_VEX, 0, 0, 10),
    CREEPER(Mob.TD_CREEPER, 0, 0, 10),
    CREEPER_CHARGED(Mob.TD_CREEPER_CHARGED, 0, 0, 10),
    SLIME(Mob.TD_SLIME, 0, 0, 10),
    MAGMA_CUBE(Mob.TD_MAGMA_CUBE, 0, 0, 10),
    PHANTOM(Mob.TD_PHANTOM, 0, 0, 10),
    DROWNED(Mob.TD_DROWNED, 0, 0, 10),
    GUARDIAN(Mob.TD_GUARDIAN, 0, 0, 10),
    ELDER_GUARDIAN(Mob.TD_ELDER_GUARDIAN, 0, 0, 10),
    WARDEN(Mob.TD_WARDEN, 0, 0, 10),
    ENDER_DRAGON(Mob.TD_ENDER_DRAGON, 0, 0, 10),
    WITHER(Mob.TD_WITHER, 0, 0, 10),
    GIANT(Mob.TD_GIANT, 0, 0, 10),

    ;

    public static final TowerDefenseMobInfo[] VALUES = values();

    private final Mob mob;
    private final int cost; // how much it costs to spawn
    private final float incomeModifier; // how it affects the income rate
    private final int spawnDelay; // ticks

    TowerDefenseMobInfo(Mob mob, int cost, float incomeModifier, int spawnDelay) {
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

    public float getIncomeModifier() {
        return incomeModifier;
    }

    public int getSpawnDelay() {
        return spawnDelay;
    }
}
