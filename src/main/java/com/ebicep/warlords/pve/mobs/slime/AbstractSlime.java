package com.ebicep.warlords.pve.mobs.slime;

import com.ebicep.customentities.nms.pve.CustomSlime;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import org.bukkit.Location;

public abstract class AbstractSlime extends AbstractMob<CustomSlime> {

    public AbstractSlime(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(new CustomSlime(spawnLocation.getWorld()), spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
    }

}
