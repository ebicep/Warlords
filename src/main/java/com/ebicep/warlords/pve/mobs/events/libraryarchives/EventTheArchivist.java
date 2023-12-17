package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import org.bukkit.Location;

public class EventTheArchivist extends AbstractMob implements BossMob {

    public EventTheArchivist(Location spawnLocation) {
        this(
                spawnLocation,
                "The Archivist",
                125000,
                0.21f,
                15,
                960,
                1075
        );
    }

    public EventTheArchivist(
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

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_THE_ARCHIVIST;
    }
}
