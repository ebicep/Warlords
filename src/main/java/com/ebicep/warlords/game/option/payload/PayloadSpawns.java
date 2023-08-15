package com.ebicep.warlords.game.option.payload;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mobs;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

public class PayloadSpawns {

    private static final double SPAWN_DISTANCE_SQUARED = 20 * 20;
    private final List<Location> spawnLocations;
    private final List<TimedSpawnWave> timedSpawnWaves;
    private final List<PayloadSpawnWave> payloadSpawnWaves;
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
            if (!payloadSpawnWaves.isEmpty() && payloadProgressCounter <= payloadSpawnWaves.size()) {
                payloadSpawnCounter = payloadProgressCounter;
                List<Location> spawnLocationsNear = getSpawnLocationsNear(payloadLocation);
                spawnMobs(spawnMethod, payloadSpawnWaves.get(payloadProgressCounter).spawns(), spawnLocationsNear);
                spawnTimer = 1;
                return;
            }
        }
        spawnTimer++;
        List<Location> spawnLocationsNear = null;
        for (TimedSpawnWave timedSpawnWave : timedSpawnWaves) {
            int spawnPerTick = timedSpawnWave.mod();
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

    private static void spawnMobs(BiConsumer<AbstractMob<?>, Team> spawnMethod, List<Pair<Integer, Mobs>> spawns, List<Location> spawnLocationsNear) {
        for (Pair<Integer, Mobs> spawn : spawns) {
            Integer spawnAmount = spawn.getA();
            Mobs mobToSpawn = spawn.getB();
            for (int i = 0; i < spawnAmount; i++) {
                int randomIndex = ThreadLocalRandom.current().nextInt(spawnLocationsNear.size());
                spawnMethod.accept(mobToSpawn.createMob.apply(spawnLocationsNear.get(randomIndex)), Team.BLUE);
            }
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

    public record TimedSpawnWave(int mod, List<Pair<Integer, Mobs>> spawns) {
        @SafeVarargs
        public TimedSpawnWave(int mod, Pair<Integer, Mobs>... spawns) {
            this(mod, Arrays.asList(spawns));
        }
    }

    public record PayloadSpawnWave(List<Pair<Integer, Mobs>> spawns) {
        @SafeVarargs
        public PayloadSpawnWave(Pair<Integer, Mobs>... spawns) {
            this(Arrays.asList(spawns));
        }
    }

}
