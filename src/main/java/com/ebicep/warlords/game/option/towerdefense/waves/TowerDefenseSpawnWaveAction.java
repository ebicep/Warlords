package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;

public class TowerDefenseSpawnWaveAction implements WaveAction<TowerDefenseOption> {

    private final Mob mob;

    public TowerDefenseSpawnWaveAction(Mob mob) {
        this.mob = mob;
    }

    @Override
    public boolean run(TowerDefenseOption pveOption) {
        for (Team team : TeamMarker.getTeams(pveOption.getGame())) {
            AbstractMob abstractMob = mob.createMob(null); // spawnLocation handled by TowerDefenseOption
            pveOption.spawnNewMob(abstractMob, team);
        }
        return true;
    }
}
