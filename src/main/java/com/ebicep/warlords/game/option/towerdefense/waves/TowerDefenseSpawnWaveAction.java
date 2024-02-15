package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;

import javax.annotation.Nullable;

public class TowerDefenseSpawnWaveAction implements WaveAction<TowerDefenseOption> {

    private final Mob mob;
    @Nullable
    private final WarlordsEntity spawner;

    public TowerDefenseSpawnWaveAction(Mob mob) {
        this(mob, null);
    }

    public TowerDefenseSpawnWaveAction(Mob mob, @Nullable WarlordsEntity warlordsEntity) {
        this.mob = mob;
        this.spawner = warlordsEntity;
    }

    @Override
    public String toString() {
        return "TowerDefenseSpawnWaveAction{" +
                "mob=" + mob +
                ", spawner=" + spawner +
                '}';
    }

    @Override
    public boolean run(TowerDefenseOption towerDefenseOption) {
        for (Team team : TeamMarker.getTeams(towerDefenseOption.getGame())) {
            if ((spawner != null && spawner.getTeam() == team) || team == Team.GAME) {
                continue;
            }
            AbstractMob abstractMob = mob.createMob(towerDefenseOption.getRandomSpawnLocation(team));
            towerDefenseOption.spawnNewMob(abstractMob, spawner);
        }
        return true;
    }

    public Mob getMob() {
        return mob;
    }
}
