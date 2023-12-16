package com.ebicep.warlords.game.option.pve.wavedefense.waves;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.java.RandomCollection;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import javax.annotation.Nullable;

public class SimpleWave implements Wave {

    private int delay;
    private final RandomCollection<SpawnSettings> randomCollection = new RandomCollection<>();
    private final int count;
    private final Component message;
    private int spawnTickPeriod = 8;

    public SimpleWave(@Nullable Component message) {
        this.delay = 0;
        this.count = 0;
        this.message = message;
    }

    public SimpleWave(int count, int delay, @Nullable Component message) {
        this.count = count;
        this.delay = delay;
        this.message = message;
    }

    public SimpleWave add(Mob factory) {
        return add(randomCollection.getSize() == 0 ? 1 : randomCollection.getTotal() / randomCollection.getSize(), factory);
    }

    public SimpleWave add(Mob factory, Location customSpawnLocation) {
        return add(randomCollection.getSize() == 0 ? 1 : randomCollection.getTotal() / randomCollection.getSize(), factory, customSpawnLocation);
    }

    public SimpleWave add(double baseWeight, Mob factory) {
        randomCollection.add(baseWeight, new SpawnSettings(baseWeight, factory, null));
        return this;
    }

    public SimpleWave add(double baseWeight, Mob factory, Location customSpawnLocation) {
        randomCollection.add(baseWeight, new SpawnSettings(baseWeight, factory, customSpawnLocation));
        return this;
    }

    @Override
    public AbstractMob spawnMonster(Location loc) {
        SpawnSettings spawnSettings = randomCollection.next();
        AbstractMob mob = spawnSettings.mob().createMob(spawnSettings.location() == null ? loc : spawnSettings.location());
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

    record SpawnSettings(double weight, Mob mob, Location location) {
    }
}
