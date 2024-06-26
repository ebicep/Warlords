package com.ebicep.warlords.pve.mobs.wolf;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import org.bukkit.Location;

public class Hound extends AbstractMob implements IntermediateMob {

    public Hound(Location spawnLocation) {
        super(
                spawnLocation,
                "Hound",
                900,
                0.5f,
                0,
                600,
                800
        );
    }

    public Hound(
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
        return Mob.HOUND;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

    }

}
