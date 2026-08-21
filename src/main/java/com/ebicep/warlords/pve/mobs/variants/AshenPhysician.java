package com.ebicep.warlords.pve.mobs.variants;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import org.bukkit.Location;

public class AshenPhysician extends AbstractMob implements IntermediateMob {

    public AshenPhysician(Location spawnLocation) {
        this(spawnLocation, "Ashen Physician", 5200, .30f, 5, 340, 480);
    }

    public AshenPhysician(Location spawnLocation, String name, int maxHealth, float walkSpeed, float damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ASHEN_PHYSICIAN;
    }
}
