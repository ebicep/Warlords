package com.ebicep.warlords.pve.mobs.witch;

import com.ebicep.customentities.nms.pve.CustomWitch;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import org.bukkit.Location;

public abstract class AbstractWitch extends AbstractMob<CustomWitch> {

    public AbstractWitch(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(new CustomWitch(spawnLocation.getWorld()), spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
    }

}
