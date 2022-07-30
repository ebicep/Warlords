package com.ebicep.warlords.game.option.wavedefense.waves;

import com.ebicep.warlords.game.option.wavedefense.PartialMonster;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class SimpleWave implements Wave {
    
    private int delay;
    private double totalWeight;
    private final List<Pair<Double, Function<Location, PartialMonster>>> entries = new ArrayList<>();
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

    public SimpleWave add(Function<Location, PartialMonster> factory) {
        return add(entries.isEmpty() ? 1 : totalWeight / entries.size(), factory);
    }

    public SimpleWave add(double baseWeight, Function<Location, PartialMonster> factory) {
        totalWeight += baseWeight;
        entries.add(new Pair<>(baseWeight, factory));
        return this;
    }

    @Override
    public PartialMonster spawnRandomMonster(Location loc, Random random) {
        double index = random.nextDouble() * totalWeight;
        for (Pair<Double, Function<Location, PartialMonster>> entry : entries) {
            if (mobTier != null && mobTier.equals(MobTier.BOSS)) {
                loc.getWorld().spigot().strikeLightningEffect(loc, false);
            }
            if (index < entry.getA()) {
                return entry.getB().apply(loc);
            }
            index -= entry.getA();
        }
        return PartialMonster.fromEntity("No monsters", WarlordsNPC.spawnZombieNoAI(loc, null));
    }

    @Override
    public PartialMonster spawnMonster(Location loc) {
        double index = totalWeight;
        for (Pair<Double, Function<Location, PartialMonster>> entry : entries) {
            if (mobTier != null && mobTier.equals(MobTier.BOSS)) {
                loc.getWorld().spigot().strikeLightningEffect(loc, false);
            }
            if (index < entry.getA()) {
                return entry.getB().apply(loc);
            }
            index -= entry.getA();
        }
        return PartialMonster.fromEntity("No monsters", WarlordsNPC.spawnZombieNoAI(loc, null));
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
