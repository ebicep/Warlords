package com.ebicep.warlords.pve.mobs.spider;

import com.ebicep.customentities.nms.pve.CustomSpider;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.flags.Spider;
import org.bukkit.Location;

public abstract class AbstractSpider extends AbstractMob<CustomSpider> implements Spider {

    public AbstractSpider(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(new CustomSpider(spawnLocation.getWorld()), spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

}
