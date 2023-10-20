package com.ebicep.warlords.game.option.pve.wavedefense.waves;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

public class DelegatingWave implements Wave {

    private final Wave w;

    public DelegatingWave(Wave w) {
        this.w = w;
    }

    @Override
    public AbstractMob spawnMonster(Location loc) {
        return w.spawnMonster(loc);
    }

    @Override
    public int getMonsterCount() {
        return w.getMonsterCount();
    }

    @Override
    public Component getMessage() {
        return w.getMessage();
    }

    @Override
    public int getDelay() {
        return w.getDelay();
    }

    @Override
    public int getSpawnTickPeriod() {
        return w.getSpawnTickPeriod();
    }

}
