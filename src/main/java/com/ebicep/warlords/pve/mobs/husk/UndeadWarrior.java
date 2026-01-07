package com.ebicep.warlords.pve.mobs.husk;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import org.bukkit.Location;

public class UndeadWarrior extends AbstractMob implements BossMob {

    public UndeadWarrior(Location spawnLocation) {
        super(
                spawnLocation,
                "Undead Warrior",
                1000,
                0.4f,
                0,
                500,
                1000
        );
    }

    public UndeadWarrior(
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
        return Mob.UNDEAD_WARRIOR;
    }

}
