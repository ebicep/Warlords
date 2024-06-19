package com.ebicep.warlords.pve.mobs.witherskeleton;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import org.bukkit.Location;

public class CelestialOpus extends AbstractMob implements EliteMob {

    public CelestialOpus(Location spawnLocation) {
        super(
                spawnLocation,
                "Celestial Opus",
                9000,
                0.4f,
                10,
                800,
                1000
        );
    }

    public CelestialOpus(
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
        return Mob.CELESTIAL_OPUS;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
    }

}
