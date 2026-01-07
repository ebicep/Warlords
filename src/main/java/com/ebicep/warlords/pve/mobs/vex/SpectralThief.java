package com.ebicep.warlords.pve.mobs.vex;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import org.bukkit.Location;

public class SpectralThief extends AbstractMob implements EliteMob {

    public SpectralThief(Location spawnLocation) {
        super(
                spawnLocation,
                "Spectral Thief",
                1000,
                0.4f,
                0,
                500,
                1000
        );
    }

    public SpectralThief(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }


    @Override
    public Mob getMobRegistry() {
        return Mob.SPECTRAL_THIEF;
    }

}
