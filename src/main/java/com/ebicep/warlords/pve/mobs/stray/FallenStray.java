package com.ebicep.warlords.pve.mobs.stray;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import org.bukkit.Location;

public class FallenStray extends AbstractMob implements IntermediateMob {

    public FallenStray(Location spawnLocation) {
        super(
                spawnLocation,
                "Fallen Stray",
                1000,
                0.4f,
                0,
                500,
                1000
        );
    }

    public FallenStray(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }


    @Override
    public Mob getMobRegistry() {
        return Mob.FALLEN_STRAY;
    }

}
