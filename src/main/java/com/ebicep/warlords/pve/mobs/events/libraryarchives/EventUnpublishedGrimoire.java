package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import org.bukkit.Location;

public class EventUnpublishedGrimoire extends AbstractMob implements IntermediateMob {

    public EventUnpublishedGrimoire(Location spawnLocation) {
        this(
                spawnLocation,
                "Unpublished Grimoire",
                3500,
                0.38f,
                0,
                375,
                575
        );
    }

    public EventUnpublishedGrimoire(
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
        return Mob.EVENT_UNPUBLISHED_GRIMOIRE;
    }
}
