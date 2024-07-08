package com.ebicep.warlords.pve.mobs.pigzombie;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class PigDisciple extends AbstractMob implements BasicMob {

    public PigDisciple(Location spawnLocation) {
        super(
                spawnLocation,
                "Pig Disciple",
                2800,
                0.42f,
                0,
                250,
                350
        );
    }

    public PigDisciple(
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
        return Mob.PIG_DISCIPLE;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

    }

}
