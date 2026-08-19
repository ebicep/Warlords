package com.ebicep.warlords.pve.mobs.variants;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import org.bukkit.Location;

public class SunkenDelver extends AbstractMob implements IntermediateMob {

    public SunkenDelver(Location spawnLocation) {
        this(spawnLocation, "Sunken Delver", 7200, .24f, 10, 420, 570);
    }

    public SunkenDelver(Location spawnLocation, String name, int maxHealth, float walkSpeed, float damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SUNKEN_DELVER;
    }
}
