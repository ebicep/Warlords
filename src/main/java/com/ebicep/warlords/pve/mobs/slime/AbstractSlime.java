package com.ebicep.warlords.pve.mobs.slime;

import com.ebicep.customentities.nms.pve.CustomSlime;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobTier;
import org.bukkit.Location;
import org.bukkit.inventory.EntityEquipment;

public abstract class AbstractSlime extends AbstractMob<CustomSlime> {
    public AbstractSlime(Location spawnLocation, String name, MobTier mobTier, EntityEquipment ee, int maxHealth, float walkSpeed, int damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(new CustomSlime(spawnLocation.getWorld()), spawnLocation, name, mobTier, ee, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }
}
