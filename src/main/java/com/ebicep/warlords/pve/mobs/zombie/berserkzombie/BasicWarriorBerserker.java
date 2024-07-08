package com.ebicep.warlords.pve.mobs.zombie.berserkzombie;

import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class BasicWarriorBerserker extends AbstractBerserkZombie implements BasicMob {

    public BasicWarriorBerserker(Location spawnLocation) {
        this(
                spawnLocation,
                "Warrior Berserker",
                2800,
                0.38f,
                0,
                200,
                300
        );
    }

    public BasicWarriorBerserker(
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
                maxMeleeDamage,
                new BerserkerZombieWoundingStrike()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.BASIC_WARRIOR_BERSERKER;
    }
}
