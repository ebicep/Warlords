package com.ebicep.warlords.pve.mobs.blaze;

import com.ebicep.customentities.nms.pve.CustomBlaze;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobTier;
import org.bukkit.Location;
import org.bukkit.inventory.EntityEquipment;

public abstract class AbstractBlaze extends AbstractMob<CustomBlaze> {

    public AbstractBlaze(
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
        super(new CustomBlaze(spawnLocation.getWorld()), spawnLocation, name, mobTier, ee, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    public AbstractBlaze(
            Location spawnLocation,
            String name,
            MobTier mobTier,
            EntityEquipment ee,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(new CustomBlaze(spawnLocation.getWorld()), spawnLocation, name, mobTier, ee, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
    }
}
