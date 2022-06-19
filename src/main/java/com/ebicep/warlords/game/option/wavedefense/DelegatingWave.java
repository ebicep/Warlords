package com.ebicep.warlords.game.option.wavedefense;

import java.util.Random;
import org.bukkit.Location;

public class DelegatingWave implements Wave {

    private final Wave w;

    public DelegatingWave(Wave w) {
        this.w = w;
    }

    @Override
    public PartialMonster spawnRandomMonster(Location loc, Random random) {
        return w.spawnRandomMonster(loc, random);
    }

    @Override
    public int getMonsterCount() {
        return w.getMonsterCount();
    }

    @Override
    public String getMessage() {
        return w.getMessage();
    }

}
