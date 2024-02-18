package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.game.option.towerdefense.towers.TowerRegistry;
import com.ebicep.warlords.game.option.towerdefense.waves.FixedPlayerWave;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains player information for the Tower Defense game, exp + stats + etc.
 */
public class TowerDefensePlayerInfo {

    // in game
    private float currentExp;
    private int currentInsigniaRate = 20; // per second
    private final FixedPlayerWave playerWave = new FixedPlayerWave(); // list of sent waves
    private BukkitTask waveTask;
    // stats
    private float totalExp;
    private float insigniaSpent;
    private final Map<TowerRegistry, TowerStats> towerStats = new HashMap<>();
    private final Map<Mob, MobStats> mobStats = new HashMap<>();

    public float getCurrentExp() {
        return currentExp;
    }

    public void addCurrentExp(float exp) {
        this.currentExp += exp;
    }

    public void setCurrentExp(float currentExp) {
        this.currentExp = currentExp;
    }

    public int getCurrentInsigniaRate() {
        return currentInsigniaRate;
    }

    public void setCurrentInsigniaRate(int currentInsigniaRate) {
        this.currentInsigniaRate = currentInsigniaRate;
    }

    public FixedPlayerWave getPlayerWave() {
        return playerWave;
    }

    public BukkitTask getWaveTask() {
        return waveTask;
    }

    public void setWaveTask(BukkitTask waveTask) {
        this.waveTask = waveTask;
    }

    public float getTotalExp() {
        return totalExp;
    }

    public float getInsigniaSpent() {
        return insigniaSpent;
    }

    public Map<TowerRegistry, TowerStats> getTowerStats() {
        return towerStats;
    }

    public Map<Mob, MobStats> getMobStats() {
        return mobStats;
    }

    static class TowerStats {
        private final Map<Integer, Integer> upgradesBought = new HashMap<>();
        private int timesBuilt;
        private int timesSold;
    }

    static class MobStats {
        private boolean unlocked = false;
        private int timesSent;
        private int timesKilled;

    }
}
