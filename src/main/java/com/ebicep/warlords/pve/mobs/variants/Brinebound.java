package com.ebicep.warlords.pve.mobs.variants;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class Brinebound extends AbstractMob implements BasicMob {

    public Brinebound(Location spawnLocation) {
        this(spawnLocation, "Brinebound", 2600, .30f, 0, 230, 320);
    }

    public Brinebound(Location spawnLocation, String name, int maxHealth, float walkSpeed, float damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.BRINEBOUND;
    }
}
