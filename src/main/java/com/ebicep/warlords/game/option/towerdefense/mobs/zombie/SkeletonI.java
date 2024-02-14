package com.ebicep.warlords.game.option.towerdefense.mobs.zombie;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.option.towerdefense.mobs.TowerDefenseMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class SkeletonI extends TowerDefenseMob implements BasicMob {

    public SkeletonI(
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

    public SkeletonI(Location spawnLocation) {
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

    @Override
    public Mob getMobRegistry() {
        return Mob.SKELETON_I;
    }
}
