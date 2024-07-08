package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import org.bukkit.Location;

public class SkeletalPyromancer extends AbstractMob implements EliteMob {

    public SkeletalPyromancer(Location spawnLocation) {
        super(
                spawnLocation,
                "Skeletal Pyromancer",
                5000,
                0.05f,
                20,
                0,
                0,
                new Fireball(5.5f), new FlameBurst(20)
        );
    }

    public SkeletalPyromancer(
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
                new Fireball(5.5f), new FlameBurst(20)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SKELETAL_PYROMANCER;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

    }

}
