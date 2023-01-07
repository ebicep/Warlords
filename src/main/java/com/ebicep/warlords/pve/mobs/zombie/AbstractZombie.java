package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.customentities.nms.pve.CustomZombie;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobTier;
import org.bukkit.Location;
import org.bukkit.inventory.EntityEquipment;

public abstract class AbstractZombie extends AbstractMob<CustomZombie> {

    public AbstractZombie(Location spawnLocation, String name, MobTier mobTier, EntityEquipment ee, int maxHealth, float walkSpeed, int damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(new CustomZombie(spawnLocation.getWorld()), spawnLocation, name, mobTier, ee, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }
}
