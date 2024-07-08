package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseSpawner;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.Mob;

import java.util.ArrayList;
import java.util.List;

public class FixedPlayerWave implements TowerDefenseWave {

    private final List<WaveAction<TowerDefenseOption>> actions = new ArrayList<>();
    private final List<WaveEndCondition> endConditions = new ArrayList<>();
    private int waveActionIndex = 0;
    private boolean sent = false;

    public int add(Mob mob, int delay, boolean fill, WarlordsEntity spawner) {
        int spawnActions = actions.size() / 2;
        int amount = fill ? Math.max(TowerDefenseSpawner.MAX_PLAYER_SPAWN_AMOUNT - (spawnActions - waveActionIndex / 2), 0) : 1;
        for (int i = 0; i < amount; i++) {
            actions.add(new TowerDefenseDelayWaveAction(delay));
            actions.add(new TowerDefenseSpawnWaveAction(mob, spawner));
        }
        return amount;
    }

    public FixedPlayerWave removeLast(Mob mob, int amount) {
        // starting from end of actions, remove all mobs matching mob until different
        for (int i = 0; i < amount; i++) {
            if (actions.isEmpty()) {
                break;
            }
            WaveAction<TowerDefenseOption> lastAction = actions.get(actions.size() - 1);
            if (lastAction instanceof TowerDefenseSpawnWaveAction spawnAction) {
                if (spawnAction.getMob() == mob) {
                    actions.remove(actions.size() - 1);
                } else {
                    break;
                }
            }
        }
        return this;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public int getCost() {
        return 0;
    }

    public void addDelay() {
        List<WaveAction<TowerDefenseOption>> newActions = new ArrayList<>();
        for (WaveAction<TowerDefenseOption> action : actions) {
            newActions.add(action);
            newActions.add(new TowerDefenseDelayWaveAction(20));
        }
        actions.clear();
        actions.addAll(newActions);
    }

    @Override
    public List<WaveAction<TowerDefenseOption>> getActions() {
        return actions;
    }

    @Override
    public List<WaveEndCondition> getEndConditions() {
        return endConditions;
    }

    @Override
    public int getWaveActionIndex() {
        return waveActionIndex;
    }

    @Override
    public void setWaveActionIndex(int index) {
        this.waveActionIndex = index;
    }

}

