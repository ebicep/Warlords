package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.customentities.nms.pve.CustomZombie;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import org.bukkit.Location;
import org.bukkit.inventory.EntityEquipment;

public abstract class AbstractZombie extends AbstractMob<CustomZombie> {

    public AbstractZombie(
            Location spawnLocation,
            String name,
            EntityEquipment ee,
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
                ee,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                abilities
        );
    }
}
