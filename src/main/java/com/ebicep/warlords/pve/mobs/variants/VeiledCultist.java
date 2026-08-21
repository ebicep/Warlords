package com.ebicep.warlords.pve.mobs.variants;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import org.bukkit.Location;

public class VeiledCultist extends AbstractMob implements IntermediateMob {

    public VeiledCultist(Location spawnLocation) {
        this(spawnLocation, "Veiled Cultist", 5800, .32f, 5, 360, 500);
    }

    public VeiledCultist(Location spawnLocation, String name, int maxHealth, float walkSpeed, float damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.VEILED_CULTIST;
    }
}
