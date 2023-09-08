package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.customentities.nms.pve.CustomZombie;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.Location;

public abstract class AbstractZombie extends AbstractMob<CustomZombie> {

    public AbstractZombie(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(
                new CustomZombie(spawnLocation.getWorld()),
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                abilities
        );
    }

    @Override
    public Mob getMobRegistry() {
        return null;
    }
}
