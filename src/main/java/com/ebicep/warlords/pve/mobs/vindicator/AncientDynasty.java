package com.ebicep.warlords.pve.mobs.vindicator;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.creaking.SovereignGuardian;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import org.bukkit.Location;

public class AncientDynasty extends AbstractMob implements EliteMob {

    public AncientDynasty(Location spawnLocation) {
        super(
                spawnLocation,
                "Ancient Dynasty",
                10000,
                0.4f,
                20,
                500,
                1000
        );
    }

    public AncientDynasty(
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
        return Mob.ANCIENT_DYNASTY;
    }
}
