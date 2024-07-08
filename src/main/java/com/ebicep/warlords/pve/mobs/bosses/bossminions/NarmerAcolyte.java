package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.RemoveTarget;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import org.bukkit.Location;

public class NarmerAcolyte extends AbstractMob implements BossMinionMob {

    public NarmerAcolyte(Location spawnLocation) {
        this(spawnLocation, "Acolyte of Narmer", 5000, 0.35f, 0, 540, 765);
    }

    public NarmerAcolyte(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new RemoveTarget(10)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.NARMER_ACOLYTE;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

    }

}
