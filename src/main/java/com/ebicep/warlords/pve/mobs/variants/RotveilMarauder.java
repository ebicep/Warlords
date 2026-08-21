package com.ebicep.warlords.pve.mobs.variants;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import org.bukkit.Location;

public class RotveilMarauder extends AbstractMob implements AdvancedMob {

    public RotveilMarauder(Location spawnLocation) {
        this(spawnLocation, "Rotveil Marauder", 14500, .27f, 15, 750, 1000);
    }

    public RotveilMarauder(Location spawnLocation, String name, int maxHealth, float walkSpeed, float damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ROTVEIL_MARAUDER;
    }
}
