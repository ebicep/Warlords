package com.ebicep.warlords.pve.mobs.wolf;

import com.ebicep.customentities.nms.pve.CustomWolf;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.Location;

public abstract class AbstractWolf extends AbstractMob<CustomWolf> {

    public AbstractWolf(Location spawnLocation, String name, int maxHealth, float walkSpeed, int damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(new CustomWolf(spawnLocation.getWorld()), spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public Mob getMobRegistry() {
        return null;
    }
}
