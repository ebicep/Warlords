package com.ebicep.warlords.game.option.towerdefense.mobs.zombie;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.option.towerdefense.mobs.TowerDefenseMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class ZombieI extends TowerDefenseMob implements BasicMob {

    public ZombieI(
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

    public ZombieI(Location spawnLocation) {
        this(
                spawnLocation,
                "Zombie",
                1000,
                .35f,
                0,
                100,
                100
        );
    }

    @Override
    public Mob getMobRegistry() {
        return null;
    }
}
