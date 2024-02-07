package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Spawned a specific order of mobs
 */
public class FixedWave implements TowerDefenseWave {

    private final List<Mob> toSpawn = new ArrayList<>();
    private final int spawnDelay;
    private final int spawnPeriod;

    public FixedWave(int spawnDelay, int spawnPeriod) {
        this.spawnDelay = spawnDelay;
        this.spawnPeriod = spawnPeriod;
    }

    public FixedWave add(Mob mob) {
        return add(mob, 1);
    }

    public FixedWave add(Mob mob, int amount) {
        for (int i = 0; i < amount; i++) {
            toSpawn.add(mob);
        }
        return this;
    }

    @Nullable
    @Override
    public AbstractMob spawnMob(Location location) {
        if (toSpawn.isEmpty()) {
            return null;
        }
        Mob mob = toSpawn.remove(0);
        return mob.createMob(location);
    }

    @Override
    public int getSpawnCount() {
        return toSpawn.size();
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
