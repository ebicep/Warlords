package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class Ghoul extends AbstractMob implements BasicMob {

    public Ghoul(Location spawnLocation) {
        super(
                spawnLocation,
                "Ghoul",
                1000,
                0.4f,
                0,
                500,
                1000
        );
    }

    public Ghoul(
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
    public void onNPCCreate() {
        super.onNPCCreate();

    }

    @Override
    public Mob getMobRegistry() {
        return Mob.GHOUL;
    }

}
