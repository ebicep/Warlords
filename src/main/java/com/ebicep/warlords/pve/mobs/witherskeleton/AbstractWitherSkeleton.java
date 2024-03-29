package com.ebicep.warlords.pve.mobs.witherskeleton;

import com.ebicep.customentities.nms.pve.CustomWitherSkeleton;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobTier;
import org.bukkit.Location;
import org.bukkit.inventory.EntityEquipment;

public abstract class AbstractWitherSkeleton extends AbstractMob<CustomWitherSkeleton> {

    public AbstractWitherSkeleton(
            Location spawnLocation,
            String name,
            MobTier mobTier,
            EntityEquipment ee,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(new CustomWitherSkeleton(spawnLocation.getWorld()),
                spawnLocation,
                name,
                mobTier,
                ee,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }
}
