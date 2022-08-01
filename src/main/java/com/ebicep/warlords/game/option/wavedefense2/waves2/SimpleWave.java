package com.ebicep.warlords.game.option.wavedefense2.waves2;

import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense2.mobs2.AbstractMob;
import com.ebicep.warlords.game.option.wavedefense2.mobs2.Mobs;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.ebicep.warlords.game.option.wavedefense2.mobs2.Mobs.BASIC_ZOMBIE;

public class SimpleWave implements Wave {

    private int delay;
    private double totalWeight;
    private final List<Pair<Double, Mobs>> entries = new ArrayList<>();
    private final int count;
    private final String message;
    private MobTier mobTier;

    public SimpleWave(int count, int delay, @Nullable String message) {
        this.count = count;
        this.delay = delay;
        this.message = message;
    }

    public SimpleWave(int count, int delay, @Nullable String message, MobTier mobTier) {
        this.count = count;
        this.delay = delay;
        this.message = message;
        this.mobTier = mobTier;
    }

    public SimpleWave add(Mobs factory) {
        return add(entries.isEmpty() ? 1 : totalWeight / entries.size(), factory);
    }

    public SimpleWave add(double baseWeight, Mobs factory) {
        totalWeight += baseWeight;
        entries.add(new Pair<>(baseWeight, factory));
        return this;
    }

    @Override
    public AbstractMob<?> spawnRandomMonster(Location loc) {
        double index = ThreadLocalRandom.current().nextDouble() * totalWeight;
        for (Pair<Double, Mobs> entry : entries) {
            if (mobTier != null && mobTier.equals(MobTier.BOSS)) {
                loc.getWorld().spigot().strikeLightningEffect(loc, false);
            }
            if (index < entry.getA()) {
                return entry.getB().createMob.apply(loc);
            }
            index -= entry.getA();
        }
        return BASIC_ZOMBIE.createMob.apply(loc);
    }

    @Override
    public AbstractMob<?> spawnMonster(Location loc) {
        double index = totalWeight;
        for (Pair<Double, Mobs> entry : entries) {
            if (mobTier != null && mobTier.equals(MobTier.BOSS)) {
                loc.getWorld().spigot().strikeLightningEffect(loc, false);
            }
            if (index < entry.getA()) {
                return entry.getB().createMob.apply(loc);
            }
            index -= entry.getA();
        }
        return BASIC_ZOMBIE.createMob.apply(loc);
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
    public String getMessage() {
        return message;
    }

    public MobTier getMobTier() {
        return mobTier;
    }
}
