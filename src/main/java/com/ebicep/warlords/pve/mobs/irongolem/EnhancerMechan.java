package com.ebicep.warlords.pve.mobs.irongolem;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import org.bukkit.Location;

public class EnhancerMechan extends AbstractMob implements AdvancedMob {

    public EnhancerMechan(Location spawnLocation) {
        super(
                spawnLocation,
                "Enhancer Mechan",
                1000,
                0.4f,
                0,
                500,
                1000
        );
    }

    public EnhancerMechan(
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
        return Mob.ENHANCER_MECHAN;
    }

}
