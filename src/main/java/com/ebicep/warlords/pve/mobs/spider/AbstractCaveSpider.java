package com.ebicep.warlords.pve.mobs.spider;

import com.ebicep.customentities.nms.pve.CustomCaveSpider;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobTier;
import org.bukkit.Location;
import org.bukkit.inventory.EntityEquipment;

public abstract class AbstractCaveSpider extends AbstractMob<CustomCaveSpider> {

    public AbstractCaveSpider(
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
        super(new CustomCaveSpider(spawnLocation.getWorld()),
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
