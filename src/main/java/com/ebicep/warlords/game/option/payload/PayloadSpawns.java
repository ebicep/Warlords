package com.ebicep.warlords.game.option.payload;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mobs;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

public class PayloadSpawns {

    private static final double SPAWN_DISTANCE_SQUARED = 20 * 20;
    private final List<Location> spawnLocations;
    private final List<TimedSpawnWave> timedSpawnWaves;
    private final List<PayloadSpawnWave> payloadSpawnWaves; //index 0 = spawn wave at 10%, index 1 = spawn wave at 20%, etc
    private int spawnTimer = 0;
    private int payloadSpawnCounter = 0;

    public PayloadSpawns(List<Location> spawnLocations, List<TimedSpawnWave> timedSpawnWaves, List<PayloadSpawnWave> payloadSpawnWaves) {
        this.spawnLocations = spawnLocations;
        this.timedSpawnWaves = timedSpawnWaves;
        this.payloadSpawnWaves = payloadSpawnWaves;
    }

    public void tick(double payloadProgress, Location payloadLocation, BiConsumer<AbstractMob<?>, Team> spawnMethod) {
        int payloadProgressCounter = (int) (payloadProgress * 10);
        if (payloadProgressCounter > payloadSpawnCounter) {
            if (!payloadSpawnWaves.isEmpty() && payloadProgressCounter - 1 < payloadSpawnWaves.size()) {
                List<Location> spawnLocationsNear = getSpawnLocationsNear(payloadLocation);
                spawnMobs(spawnMethod, payloadSpawnWaves.get(payloadSpawnCounter).spawns(), spawnLocationsNear);
                payloadSpawnCounter = payloadProgressCounter;
                spawnTimer = 1;
                return;
            }
        }
        spawnTimer++;
        List<Location> spawnLocationsNear = null;
        for (TimedSpawnWave timedSpawnWave : timedSpawnWaves) {
            int spawnPerTick = timedSpawnWave.perSecond() * 20;
            if (spawnTimer % spawnPerTick != 0) {
                continue;
            }
            if (spawnLocationsNear == null) {
                spawnLocationsNear = getSpawnLocationsNear(payloadLocation);
            }
            List<Pair<Integer, Mobs>> spawns = timedSpawnWave.spawns();
            spawnMobs(spawnMethod, spawns, spawnLocationsNear);
        }
    }

    public List<Location> getSpawnLocationsNear(Location location) {
        List<Location> locations = new ArrayList<>();
        for (Location spawnLocation : spawnLocations) {
            if (spawnLocation.distanceSquared(location) <= SPAWN_DISTANCE_SQUARED) {
                locations.add(spawnLocation);
            }
        }
        return locations;
    }

    private static void spawnMobs(BiConsumer<AbstractMob<?>, Team> spawnMethod, List<Pair<Integer, Mobs>> spawns, List<Location> spawnLocationsNear) {
        int size = spawnLocationsNear.size();
        if (size == 0) {
            return;
        }
        for (Pair<Integer, Mobs> spawn : spawns) {
            Integer spawnAmount = spawn.getA();
            Mobs mobToSpawn = spawn.getB();
            for (int i = 0; i < spawnAmount; i++) {
                int randomIndex = ThreadLocalRandom.current().nextInt(size);
                spawnMethod.accept(mobToSpawn.createMob.apply(spawnLocationsNear.get(randomIndex)), Team.RED);
            }
        }
    }


    public void renderSpawnLocations() {
        for (Location spawnLocation : spawnLocations) {
            EffectUtils.displayParticle(Particle.VILLAGER_HAPPY, spawnLocation.clone().add(0, .5, 0), 1);
        }
    }

    public record TimedSpawnWave(int perSecond, List<Pair<Integer, Mobs>> spawns) {
        @SafeVarargs
        public TimedSpawnWave(int perSecond, Pair<Integer, Mobs>... spawns) {
            this(perSecond, Arrays.asList(spawns));
        }
    }

    public record PayloadSpawnWave(List<Pair<Integer, Mobs>> spawns) {
        @SafeVarargs
        public PayloadSpawnWave(Pair<Integer, Mobs>... spawns) {
            this(Arrays.asList(spawns));
        }
    }

}
