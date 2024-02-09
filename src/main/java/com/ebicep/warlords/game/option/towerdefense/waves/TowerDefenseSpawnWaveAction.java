package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;

import javax.annotation.Nullable;

public class TowerDefenseSpawnWaveAction implements WaveAction<TowerDefenseOption> {

    private final Mob mob;
    @Nullable
    private final Team fromTeam; // team that spawned the mob, null if spawned from game

    public TowerDefenseSpawnWaveAction(Mob mob) {
        this(mob, null);
    }

    public TowerDefenseSpawnWaveAction(Mob mob, @Nullable Team team) {
        this.mob = mob;
        this.fromTeam = team;
    }

    @Override
    public boolean run(TowerDefenseOption pveOption) {
        for (Team team : TeamMarker.getTeams(pveOption.getGame())) {
            if (fromTeam != null && fromTeam == team) {
                continue;
            }
            AbstractMob abstractMob = mob.createMob(null); // spawnLocation handled by TowerDefenseOption
            pveOption.spawnNewMob(abstractMob, team);
        }
        return true;
    }
}
