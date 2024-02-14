package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;

import javax.annotation.Nonnull;

public class TowerDefenseSpawnWaveAction implements WaveAction<TowerDefenseOption> {

    private final Mob mob;
    @Nonnull
    private final Team fromTeam; // team that spawned the mob, GAME if spawned from game

    public TowerDefenseSpawnWaveAction(Mob mob) {
        this(mob, Team.GAME);
    }

    public TowerDefenseSpawnWaveAction(Mob mob, @Nonnull Team team) {
        this.mob = mob;
        this.fromTeam = team;
    }

    @Override
    public boolean run(TowerDefenseOption towerDefenseOption) {
        for (Team team : TeamMarker.getTeams(towerDefenseOption.getGame())) {
            if (fromTeam == team) {
                continue;
            }
            AbstractMob abstractMob = mob.createMob(towerDefenseOption.getRandomSpawnLocation(team));
            towerDefenseOption.spawnNewMob(abstractMob, fromTeam);
        }
        return true;
    }

    public Mob getMob() {
        return mob;
    }
}
