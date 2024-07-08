package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class TDSkeleton extends TowerDefenseMob implements BasicMob {

    public TDSkeleton(Location spawnLocation) {
        this(
                spawnLocation,
                "Skeleton",
                500,
                .25f,
                10,
                200,
                200
        );
    }

    public TDSkeleton(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.TD_SKELETON;
    }

    @Override
    public double getDefaultAttackRange() {
        return 6;
    }

}
