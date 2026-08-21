package com.ebicep.warlords.game.option.pve.effigytrails;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.java.JavaUtils;
import org.bukkit.Location;

import java.util.Set;

public class EffigySpawnManager {

    private final PveOption pveOption;
    private final Set<Mob> spawnableMobs;

    public EffigySpawnManager(PveOption pveOption, Set<Mob> spawnableMobs) {
        this.pveOption = pveOption;
        this.spawnableMobs = spawnableMobs;
    }

    private void runEveryTick(int ticksElapsed) {
        if (ticksElapsed % 20 == 0) {
            if (pveOption.mobCount() < maxSpawnCount()) {
                spawnMob();
            }
        }
    }

    private void spawnMob() {
        Location spawnLocation = pveOption.getRandomSpawnLocation(Team.RED);
        pveOption.spawnNewMob(JavaUtils.randomFromSet(spawnableMobs).createMob(spawnLocation));
    }

    private int maxSpawnCount() {
        if (pveOption.playerCount() == 1) {
            return 15;
        }
        return 30; // TODO
    }

}
