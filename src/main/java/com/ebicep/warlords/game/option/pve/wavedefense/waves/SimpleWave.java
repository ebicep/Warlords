package com.ebicep.warlords.game.option.pve.wavedefense.waves;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.Mobs;
import com.ebicep.warlords.util.java.RandomCollection;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import javax.annotation.Nullable;

public class SimpleWave implements Wave {

    private int delay;
    private final RandomCollection<SpawnSettings> randomCollection = new RandomCollection<>();
    private final int count;
    private final Component message;
    private MobTier mobTier;

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

    public SimpleWave(int count, int delay, @Nullable Component message, MobTier mobTier) {
        this.count = count;
        this.delay = delay;
        this.message = message;
        this.mobTier = mobTier;
    }

    public SimpleWave add(Mobs factory) {
        return add(randomCollection.getSize() == 0 ? 1 : randomCollection.getTotal() / randomCollection.getSize(), factory);
    }

    public SimpleWave add(Mobs factory, Location customSpawnLocation) {
        return add(randomCollection.getSize() == 0 ? 1 : randomCollection.getTotal() / randomCollection.getSize(), factory, customSpawnLocation);
    }

    public SimpleWave add(double baseWeight, Mobs factory) {
        randomCollection.add(baseWeight, new SpawnSettings(baseWeight, factory, null));
        return this;
    }

    public SimpleWave add(double baseWeight, Mobs factory, Location customSpawnLocation) {
        randomCollection.add(baseWeight, new SpawnSettings(baseWeight, factory, customSpawnLocation));
        return this;
    }

    @Override
    public AbstractMob<?> spawnRandomMonster(Location loc) {
        SpawnSettings spawnSettings = randomCollection.next();
        if (mobTier != null && mobTier.equals(MobTier.BOSS)) {
            loc.getWorld().spigot().strikeLightningEffect(loc, false);
        }
        return spawnSettings.mob().createMob.apply(spawnSettings.location() == null ? loc : spawnSettings.location());
    }

    @Override
    public AbstractMob<?> spawnMonster(Location loc) {
        return spawnRandomMonster(loc);
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
    public Component getMessage() {
        return message;
    }

    public MobTier getMobTier() {
        return mobTier;
    }

    record SpawnSettings(double weight, Mobs mob, Location location) {
    }
}
