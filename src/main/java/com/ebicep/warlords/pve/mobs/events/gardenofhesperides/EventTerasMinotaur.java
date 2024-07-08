package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.abilities.GroundSlamBerserker;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import org.bukkit.Location;

public class EventTerasMinotaur extends AbstractMob implements BossMinionMob, Teras {

    public EventTerasMinotaur(Location spawnLocation) {
        this(spawnLocation, "Teras Minotaur", 2800, 0.38f, 10, 400, 600);
    }

    public EventTerasMinotaur(
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
                new GroundSlamBerserker(5, 5)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_TERAS_MINOTAUR;
    }

}
