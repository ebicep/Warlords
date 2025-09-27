package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import org.bukkit.Location;

public class Centurion extends AbstractMob implements BossMob {

    public Centurion(Location spawnLocation) {
        super(spawnLocation,
                "Centurion",
                350000,
                0.3f,
                30,
                1000,
                1500
        );
    }

    public Centurion(
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
        return Mob.CENTURION;
    }
}
