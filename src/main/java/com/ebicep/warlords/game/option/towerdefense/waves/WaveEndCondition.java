package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.game.Team;

import java.util.HashMap;
import java.util.Map;

/**
 * Condition for when a wave should be considered complete
 */
public interface WaveEndCondition {

    static WaveEndCondition allMobsDeadAnySide() {
        return towerDefenseWave -> {
            Map<Team, Integer> teamAliveMobs = new HashMap<>();
            towerDefenseWave.getActions()
                            .stream()
                            .filter(TowerDefenseSpawnWaveAction.class::isInstance)
                            .map(TowerDefenseSpawnWaveAction.class::cast)
                            .map(TowerDefenseSpawnWaveAction::getSpawnedMobs)
                            .forEach(spawnedMobs -> spawnedMobs.forEach((team, mob) -> {
                                teamAliveMobs.putIfAbsent(team, 0);
                                if (mob.getWarlordsNPC().isAlive()) {
                                    teamAliveMobs.merge(team, 1, Integer::sum);
                                }
                            }));
            return teamAliveMobs.values().stream().anyMatch(i -> i == 0);
        };
    }

    boolean isWaveDone(TowerDefenseWave towerDefenseWave);

}
