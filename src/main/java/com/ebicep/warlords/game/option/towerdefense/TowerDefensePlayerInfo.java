package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.game.option.towerdefense.towers.TowerRegistry;
import com.ebicep.warlords.game.option.towerdefense.waves.FixedPlayerWave;
import com.ebicep.warlords.pve.mobs.Mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains player information for the Tower Defense game, exp + stats + etc.
 */
public class TowerDefensePlayerInfo {

    // in game
    private float currentExp;
    private int currentInsigniaRate = 20; // per second
    private final List<FixedPlayerWave> playerWaves = new ArrayList<>(); // list of sent waves
    private final Map<TowerDefenseMenu.MobGroup, Integer> unlockedMobUpgrades = new HashMap<>(); // upgrade level of each mob group
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

    public int getCurrentInsigniaRate() {
        return currentInsigniaRate;
    }

    public void setCurrentInsigniaRate(int currentInsigniaRate) {
        this.currentInsigniaRate = currentInsigniaRate;
    }

    public List<FixedPlayerWave> getPlayerWaves() {
        return playerWaves;
    }

    public Map<TowerDefenseMenu.MobGroup, Integer> getUnlockedMobUpgrades() {
        return unlockedMobUpgrades;
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
