package com.ebicep.warlords.pve.mobs.variants;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class FrostboneStalker extends AbstractMob implements BasicMob {

    public FrostboneStalker(Location spawnLocation) {
        this(spawnLocation, "Frostbone Stalker", 2400, .32f, 0, 180, 260);
    }

    public FrostboneStalker(Location spawnLocation, String name, int maxHealth, float walkSpeed, float damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.FROSTBONE_STALKER;
    }
}
