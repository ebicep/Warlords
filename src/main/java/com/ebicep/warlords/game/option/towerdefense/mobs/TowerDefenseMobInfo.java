package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.warlords.pve.mobs.Mob;

public enum TowerDefenseMobInfo {

    ZOMBIE(Mob.TD_ZOMBIE, 12, 1, 40, 1),
    ZOMBIE_BABY(Mob.TD_ZOMBIE_BABY, 12, 1, 40, 1),
    ZOMBIE_VILLAGER(Mob.TD_ZOMBIE_VILLAGER, 16, 1.3f, 40, 3),
    SILVERFISH(Mob.TD_SILVERFISH, 18, 1.3f, 20, 4),
    SKELETON(Mob.TD_SKELETON, 30, 1.6f, 40, 4),
    SPIDER(Mob.TD_SPIDER, 24, 1.6f, 40, 5),
    CAVE_SPIDER(Mob.TD_CAVE_SPIDER, 27, 1.8f, 40, 6),
    ENDERMITE(Mob.TD_ENDERMITE, 34, 2.1f, 20, 7),
    HUSK(Mob.TD_HUSK, 40, 2.4f, 50, 7),
    PIGLIN(Mob.TD_PIGLIN, 48, 2.7f, 30, 9),
    ZOMBIFIED_PIGLIN(Mob.TD_ZOMBIFIED_PIGLIN, 48, 2.7f, 30, 9),
    STRAY(Mob.TD_STRAY, 60, 2.7f, 50, 9),
    WITCH(Mob.TD_WITCH, 120, 3.6f, 60, 11),
    PILLAGER(Mob.TD_PILLAGER, 65, 3.6f, 30, 11),
    PHANTOM(Mob.TD_PHANTOM, 60, 3, 20, 11),
    CREEPER(Mob.TD_CREEPER, 120, 3.6f, 50, 12),
    ENDERMAN(Mob.TD_ENDERMAN, 130, 4, 40, 13),
    HOGLIN(Mob.TD_HOGLIN, 130, 4, 40, 13),
    ZOGLIN(Mob.TD_ZOGLIN, 130, 4, 40, 13),
    BLAZE(Mob.TD_BLAZE, 120, 3.8f, 40, 14),
    GUARDIAN(Mob.TD_GUARDIAN, 160, 3.8f, 40, 14),
    WITHER_SKELETON(Mob.TD_WITHER_SKELETON, 175, 2.5f, 60, 16),
    VINDICATOR(Mob.TD_VINDICATOR, 175, 2.5f, 60, 16),
    PIGLIN_BRUTE(Mob.TD_PIGLIN_BRUTE, 175, 2.5f, 60, 17),
    CREEPER_CHARGED(Mob.TD_CREEPER_CHARGED, 220, 2, 50, 17),
    ILLUSIONER(Mob.TD_ILLUSIONER, 300, 1, 60, 18),
    GHAST(Mob.TD_GHAST, 260, 1, 70, 18),
    RAVAGER(Mob.TD_RAVAGER, 280, 0, 60, 19),
    DROWNED(Mob.TD_DROWNED, 340, 0, 60, 20),
    EVOKER(Mob.TD_EVOKER, 450, 0, 70, 21),
    VEX(Mob.TD_VEX, 0, 0, 0, 0),
    SLIME(Mob.TD_SLIME, 600, -20, 80, 23),
    MAGMA_CUBE(Mob.TD_MAGMA_CUBE, 1200, -50, 90, 26),
    ELDER_GUARDIAN(Mob.TD_ELDER_GUARDIAN, 3000, -450, 120, 30),
    WARDEN(Mob.TD_WARDEN, 3500, -450, 120, 30),
    GIANT(Mob.TD_GIANT, 4500, -600, 140, 33),
    ENDER_DRAGON(Mob.TD_ENDER_DRAGON, 6000, -800, 140, 35),
    WITHER(Mob.TD_WITHER, 6000, -800, 140, 35),

    ;

    public static final TowerDefenseMobInfo[] VALUES = values();

    private final Mob mob;
    private final int cost; // how much it costs to spawn
    private final float incomeModifier; // how it affects the income rate
    private final int spawnDelay; // ticks
    private final int waveUnlocked;

    TowerDefenseMobInfo(Mob mob, int cost, float incomeModifier, int spawnDelay, int waveUnlocked) {
        this.mob = mob;
        this.cost = cost;
        this.incomeModifier = incomeModifier;
        this.spawnDelay = spawnDelay;
        this.waveUnlocked = waveUnlocked;
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

    public int getWaveUnlocked() {
        return waveUnlocked;
    }

}
