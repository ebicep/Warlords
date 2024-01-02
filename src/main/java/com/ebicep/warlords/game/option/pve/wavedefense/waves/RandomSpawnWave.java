package com.ebicep.warlords.game.option.pve.wavedefense.waves;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.java.RandomCollection;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RandomSpawnWave implements Wave {

    private final RandomCollection<SpawnSettings> randomCollection = new RandomCollection<>();
    private final int count;
    private final Component message;
    private int delay;
    private int spawnTickPeriod = 8;


    public RandomSpawnWave(@Nullable Component message) {
        this.delay = 0;
        this.count = 0;
        this.message = message;
    }

    public RandomSpawnWave(int count, int delay, @Nullable Component message) {
        this.count = count;
        this.delay = delay;
        this.message = message;
    }

    public RandomSpawnWave add(Mob factory) {
        return add(randomCollection.getSize() == 0 ? 1 : randomCollection.getTotal() / randomCollection.getSize(), factory);
    }

    public RandomSpawnWave add(double baseWeight, Mob factory) {
        randomCollection.add(baseWeight, new SpawnSettings(baseWeight, factory, null));
        return this;
    }

    public RandomSpawnWave add(Mob factory, Location customSpawnLocation) {
        return add(randomCollection.getSize() == 0 ? 1 : randomCollection.getTotal() / randomCollection.getSize(), factory, customSpawnLocation);
    }

    public RandomSpawnWave add(double baseWeight, Mob factory, Location customSpawnLocation) {
        randomCollection.add(baseWeight, new SpawnSettings(baseWeight, factory, customSpawnLocation));
        return this;
    }

    public RandomSpawnWave add(double baseWeight, int maxSpawnTimes, Mob factory) {
        randomCollection.add(baseWeight, new SpawnSettings(baseWeight, maxSpawnTimes, factory, null));
        return this;
    }

    public RandomSpawnWave add(double baseWeight, int maxSpawnTimes, Mob factory, Location... customSpawnLocation) {
        randomCollection.add(baseWeight, new SpawnSettings(baseWeight, maxSpawnTimes, factory, List.of(customSpawnLocation)));
        return this;
    }

    public RandomSpawnWave add(double baseWeight, int maxSpawnTimes, Mob factory, List<Location> customSpawnLocation) {
        randomCollection.add(baseWeight, new SpawnSettings(baseWeight, maxSpawnTimes, factory, customSpawnLocation));
        return this;
    }

    @Override
    public AbstractMob spawnMonster(Location loc) {
        SpawnSettings spawnSettings = randomCollection.next();
        if (spawnSettings.incrementTimesSpawned()) {
            // recreate the random collection without the spawn settings that have reached their max spawn times
            RandomCollection<SpawnSettings> newRandomCollection = new RandomCollection<>();
            for (Map.Entry<Double, SpawnSettings> entry : randomCollection.getMap().entrySet()) {
                if (entry.getValue() != spawnSettings) {
                    newRandomCollection.add(entry.getKey(), entry.getValue());
                }
            }
            randomCollection.getMap().clear();
            randomCollection.getMap().putAll(newRandomCollection.getMap());
            randomCollection.setTotal(newRandomCollection.getTotal());
        }
        AbstractMob mob = spawnSettings.mob().createMob(spawnSettings.spawnLocations() == null ? loc : spawnSettings.getRandomSpawnLocation());
        if (mob instanceof BossMob) {
            loc.getWorld().strikeLightningEffect(loc);
        }
        return mob;
    }

    @Override
    public int getMonsterCount() {
        return count;
    }

    @Override
    public int getDelay() {
        return delay;
    }

    @Override
    public int getSpawnTickPeriod() {
        return spawnTickPeriod;
    }

    @Override
    public Component getMessage() {
        return message;
    }

    @Override
    public void tick(PveOption pveOption, int ticksElapsed) {

    }

    static final class SpawnSettings {
        private final double weight;
        private final int maxSpawnTimes;
        private final Mob mob;
        private final List<Location> spawnLocations;
        private int timesSpawned = 0;

        public SpawnSettings(double weight, Mob mob, Location location) {
            this(weight, Integer.MAX_VALUE, mob, location == null ? null : List.of(location));
        }

        public SpawnSettings(double weight, int maxSpawnTimes, Mob mob, List<Location> spawnLocations) {
            this.weight = weight;
            this.maxSpawnTimes = maxSpawnTimes;
            this.mob = mob;
            this.spawnLocations = spawnLocations;
        }

        public Location getRandomSpawnLocation() {
            return spawnLocations.get(ThreadLocalRandom.current().nextInt(spawnLocations.size()));
        }

        public double weight() {
            return weight;
        }

        public int maxSpawnTimes() {
            return maxSpawnTimes;
        }

        public Mob mob() {
            return mob;
        }

        public List<Location> spawnLocations() {
            return spawnLocations;
        }

        public int getTimesSpawned() {
            return timesSpawned;
        }

        public boolean incrementTimesSpawned() {
            timesSpawned++;
            return timesSpawned >= maxSpawnTimes;
        }
    }
}
