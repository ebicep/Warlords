package com.ebicep.warlords.game.option.wavedefense.waves;

import com.ebicep.warlords.game.option.wavedefense.mobs.AbstractMob;
import org.bukkit.Location;

public class DelegatingWave implements Wave {

    private final Wave w;

    public DelegatingWave(Wave w) {
        this.w = w;
    }

    @Override
    public AbstractMob<?> spawnRandomMonster(Location loc) {
        return w.spawnRandomMonster(loc);
    }

    @Override
    public AbstractMob<?> spawnMonster(Location loc) {
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
