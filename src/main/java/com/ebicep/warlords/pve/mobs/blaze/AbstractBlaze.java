package com.ebicep.warlords.pve.mobs.blaze;

import com.ebicep.customentities.nms.pve.CustomBlaze;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.Location;

public abstract class AbstractBlaze extends AbstractMob<CustomBlaze> {
    public AbstractBlaze(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(new CustomBlaze(spawnLocation.getWorld()), spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
    }

    @Override
    public Mob getMobRegistry() {
        return null;
    }
}
