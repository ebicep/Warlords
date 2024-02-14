package com.ebicep.warlords.game.option.towerdefense.mobs.zombie;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.option.towerdefense.mobs.TowerDefenseMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class ZombieII extends TowerDefenseMob implements BasicMob {

    public ZombieII(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
    }

    public ZombieII(Location spawnLocation) {
        this(
                spawnLocation,
                "Zombie",
                2000,
                .32f,
                5,
                150,
                150
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ZOMBIE_II;
    }
}
