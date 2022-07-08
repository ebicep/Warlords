package com.ebicep.warlords.game.option.wavedefense;

import org.bukkit.Location;

import java.util.Random;

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
    public PartialMonster spawnMonster(Location loc) {
        return w.spawnMonster(loc);
    }

    @Override
    public int getMonsterCount() {
        return w.getMonsterCount();
    }

    @Override
    public String getMessage() {
        return w.getMessage();
    }

    @Override
    public int getDelay() {
        return w.getDelay();
    }

}
