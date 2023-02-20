package com.ebicep.warlords.pve.mobs.witch;

import com.ebicep.customentities.nms.pve.CustomWitch;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobTier;
import org.bukkit.Location;
import org.bukkit.inventory.EntityEquipment;

public abstract class AbstractWitch extends AbstractMob<CustomWitch> {

    public AbstractWitch(Location spawnLocation, String name, MobTier mobTier, EntityEquipment ee, int maxHealth, float walkSpeed, int damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(new CustomWitch(spawnLocation.getWorld()), spawnLocation, name, mobTier, ee, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }
}
