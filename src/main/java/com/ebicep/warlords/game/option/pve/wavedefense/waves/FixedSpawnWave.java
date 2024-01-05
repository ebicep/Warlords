package com.ebicep.warlords.game.option.pve.wavedefense.waves;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FixedSpawnWave implements Wave {

    private final int delay;
    private final List<SpawnSetting> spawnSettings = new ArrayList<>();
    private final Component message;
    private int spawnCounter = 0;
    private int spawnTickPeriod;

    public FixedSpawnWave(@Nullable Component message) {
        this(0, message);
    }

    public FixedSpawnWave(int delay, @Nullable Component message) {
        this(delay, 8, message);
    }

    public FixedSpawnWave(int delay, int spawnTickPeriod, @Nullable Component message) {
        this.delay = delay;
        this.message = message;
        this.spawnTickPeriod = spawnTickPeriod;
    }

    public FixedSpawnWave add(Mob factory) {
        return add(factory, null);
    }

    public FixedSpawnWave add(Mob factory, Location customSpawnLocation) {
        spawnSettings.add(new SpawnSetting(1, factory, customSpawnLocation));
        return this;
    }

    @Override
    public @Nullable AbstractMob spawnMonster(Location loc) {
        if (spawnCounter >= spawnSettings.size()) {
            spawnCounter = 0;
        }
        SpawnSetting spawnSetting = spawnSettings.get(spawnCounter++);
        AbstractMob mob = spawnSetting.mob.createMob(spawnSetting.location() == null ? loc : spawnSetting.location());
        if (mob instanceof BossMob) {
            loc.getWorld().strikeLightningEffect(loc);
        }
        return mob;
    }

    @Override
    public int getMonsterCount() {
        return spawnSettings.stream().mapToInt(SpawnSetting::amount).sum();
    }

    @Override
    public int getDelay() {
        return delay;
    }

    @Override
    public int getSpawnTickPeriod() {
        return 0;
    }

    @Override
    public Component getMessage() {
        return message;
    }

    @Override
    public void tick(PveOption pveOption, int ticksElapsed) {

    }

    record SpawnSetting(int amount, Mob mob, Location location) {
    }

}
