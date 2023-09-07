package com.ebicep.warlords.pve.mobs.irongolem;

import com.ebicep.customentities.nms.pve.CustomIronGolem;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import org.bukkit.Location;
import org.bukkit.inventory.EntityEquipment;

public abstract class AbstractIronGolem extends AbstractMob<CustomIronGolem> {

    public AbstractIronGolem(
            Location spawnLocation,
            String name,
            EntityEquipment ee,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(new CustomIronGolem(spawnLocation.getWorld()), spawnLocation, name, ee, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }
}
