package com.ebicep.warlords.pve.mobs.witherskeleton;

import com.ebicep.customentities.nms.pve.CustomWitherSkeleton;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.Location;

public abstract class AbstractWitherSkeleton extends AbstractMob<CustomWitherSkeleton> {

    public AbstractWitherSkeleton(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(new CustomWitherSkeleton(spawnLocation.getWorld()),
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
        return null;
    }
}
