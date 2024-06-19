package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import org.bukkit.Location;

public class ArachnoVeneratus extends AbstractMob implements BossMinionMob {

    public ArachnoVeneratus(Location spawnLocation) {
        this(spawnLocation, "Arachno Veneratus", 3000, 0.45f, 5, 350, 500);
    }

    public ArachnoVeneratus(
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
        return Mob.ARACHNO_VENERATUS;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

    }

}
