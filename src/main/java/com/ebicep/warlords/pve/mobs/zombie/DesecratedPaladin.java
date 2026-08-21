package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import org.bukkit.Location;

public class DesecratedPaladin extends AbstractMob implements ChampionMob {

    public DesecratedPaladin(Location spawnLocation) {
        super(
                spawnLocation,
                "Desecrated Paladin",
                1000,
                0.4f,
                0,
                500,
                1000
        );
    }

    public DesecratedPaladin(
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
        return Mob.DESECRATED_PALADIN;
    }

}
