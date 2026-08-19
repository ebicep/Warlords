package com.ebicep.warlords.pve.mobs.variants;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import org.bukkit.Location;

public class GraveKnight extends AbstractMob implements IntermediateMob {

    public GraveKnight(Location spawnLocation) {
        this(spawnLocation, "Grave Knight", 6500, .28f, 8, 380, 520);
    }

    public GraveKnight(Location spawnLocation, String name, int maxHealth, float walkSpeed, float damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.GRAVE_KNIGHT;
    }
}
