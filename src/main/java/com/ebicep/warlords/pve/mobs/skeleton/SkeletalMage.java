package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class SkeletalMage extends AbstractMob implements BasicMob {

    public SkeletalMage(Location spawnLocation) {
        super(
                spawnLocation,
                "Skeletal Mage",
                1600,
                0.05f,
                0,
                0,
                0,
                new Fireball(5.5f)
        );
    }

    public SkeletalMage(
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
                new Fireball(5.5f)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SKELETAL_MAGE;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

    }

}
