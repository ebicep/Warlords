package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class ZombieLancer extends AbstractMob implements BasicMob {

    public ZombieLancer(Location spawnLocation) {
        super(
                spawnLocation,
                "Zombie Lancer",
                2800,
                0.38f,
                0,
                200,
                300
        );
    }

    public ZombieLancer(
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
        return Mob.ZOMBIE_LANCER;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

    }

}
