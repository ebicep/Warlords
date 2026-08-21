package com.ebicep.warlords.pve.mobs.stray;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class Stray extends AbstractMob implements BasicMob {

    public Stray(Location spawnLocation) {
        super(
                spawnLocation,
                "Stray",
                1000,
                0.4f,
                0,
                500,
                1000
        );
    }

    public Stray(
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
        return Mob.STRAY;
    }

}
