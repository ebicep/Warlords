package com.ebicep.warlords.pve.mobs.slime;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import org.bukkit.Location;

public class LurkingSlime extends AbstractMob implements AdvancedMob {

    public LurkingSlime(Location spawnLocation) {
        super(
                spawnLocation,
                "Lurking Slime",
                1000,
                0.4f,
                0,
                500,
                1000
        );
    }

    public LurkingSlime(
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
        return Mob.LURKING_SLIME;
    }

}
