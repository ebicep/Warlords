package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class TDZombie extends TowerDefenseMob implements BasicMob {

    public TDZombie(
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

    public TDZombie(Location spawnLocation) {
        this(
                spawnLocation,
                "Zombie",
                1000,
                .3f,
                0,
                100,
                100
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.TD_ZOMBIE;
    }
}
