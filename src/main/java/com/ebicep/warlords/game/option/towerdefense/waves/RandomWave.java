package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.java.RandomCollection;
import org.bukkit.Location;

import javax.annotation.Nullable;

/**
 * Randomly spawns a mob based on its weight
 */
public class RandomWave implements TowerDefenseWave {

    private final RandomCollection<Mob> mobWeights = new RandomCollection<>();
    private final int spawnCount;
    private final int spawnDelay;
    private final int spawnPeriod;

    public RandomWave(int spawnCount, int spawnDelay, int spawnPeriod) {
        this.spawnCount = spawnCount;
        this.spawnDelay = spawnDelay;
        this.spawnPeriod = spawnPeriod;
    }

    public RandomWave add(Mob mob, double weight) {
        mobWeights.add(weight, mob);
        return this;
    }

    @Override
    @Nullable
    public AbstractMob spawnMob(Location location) {
        Mob mob = mobWeights.next();
        if (mob == null) {
            return null;
        }
        return mob.createMob(location);
    }

    @Override
    public int getSpawnCount() {
        return spawnCount;
    }

    @Override
    public int getSpawnDelay() {
        return spawnDelay;
    }

    @Override
    public int getSpawnPeriod() {
        return spawnPeriod;
    }

}
