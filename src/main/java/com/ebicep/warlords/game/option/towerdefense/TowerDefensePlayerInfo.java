package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.game.option.towerdefense.towers.TowerRegistry;
import com.ebicep.warlords.game.option.towerdefense.waves.FixedPlayerWave;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains player information for the Tower Defense game, exp + stats + etc.
 */
public class TowerDefensePlayerInfo {

    // in game
    private float incomeRate = 20; // per second
    private final FixedPlayerWave playerWave = new FixedPlayerWave(); // list of sent waves
    private BukkitTask waveTask;
    @Nullable
    private BukkitTask renderTask; // whatever the player is currently rendering (tower radius, etc)
    // stats
    private float totalIncomeGained;
    private float totalIncomeSpent;
    private final Map<TowerRegistry, TowerStats> towerStats = new HashMap<>();
    private final Map<Mob, MobStats> mobStats = new HashMap<>();

    public float getIncomeRate() {
        return incomeRate;
    }

    public void addIncomeRate(float incomeRate) {
        this.incomeRate += incomeRate;
    }

    public void setIncomeRate(float incomeRate) {
        this.incomeRate = incomeRate;
    }

    public FixedPlayerWave getPlayerWave() {
        return playerWave;
    }

    public BukkitTask getWaveTask() {
        return waveTask;
    }

    public void setRenderTask(@Nullable BukkitTask renderTask) {
        this.renderTask = renderTask;
    }

    public @Nullable BukkitTask getRenderTask() {
        return renderTask;
    }

    public void setWaveTask(BukkitTask waveTask) {
        this.waveTask = waveTask;
    }

    public float getTotalIncomeGained() {
        return totalIncomeGained;
    }

    public float getTotalIncomeSpent() {
        return totalIncomeSpent;
    }

    public Map<TowerRegistry, TowerStats> getTowerStats() {
        return towerStats;
    }

    public Map<Mob, MobStats> getMobStats() {
        return mobStats;
    }

    public static class TowerStats {
        private final Map<Integer, Integer> upgradesBought = new HashMap<>();
        private int timesBuilt;
        private int timesSold;
    }

    public static class MobStats {
        private boolean unlocked = false;
        private int timesSent;
        private int timesKilled;

    }
}
