package com.ebicep.warlords.pve.mobs.variants;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class SaltbloodCorsair extends AbstractMob implements BasicMob {

    public SaltbloodCorsair(Location spawnLocation) {
        this(spawnLocation, "Saltblood Corsair", 2800, .31f, 0, 180, 270);
    }

    public SaltbloodCorsair(Location spawnLocation, String name, int maxHealth, float walkSpeed, float damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SALTBLOOD_CORSAIR;
    }
}
