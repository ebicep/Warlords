package com.ebicep.warlords.game.option.wavedefense.mobs.skeleton;

import com.ebicep.customentities.nms.pve.CustomSkeleton;
import com.ebicep.warlords.game.option.wavedefense.mobs.AbstractMob;
import org.bukkit.Location;
import org.bukkit.inventory.EntityEquipment;

public abstract class AbstractSkeleton extends AbstractMob<CustomSkeleton> {

    public AbstractSkeleton(Location spawnLocation, String name, EntityEquipment ee, int maxHealth, float walkSpeed, int damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(new CustomSkeleton(spawnLocation.getWorld()), spawnLocation, name, ee, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }
}
