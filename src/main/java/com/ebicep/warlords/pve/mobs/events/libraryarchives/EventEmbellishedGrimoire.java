package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import org.bukkit.Location;

public class EventEmbellishedGrimoire extends AbstractMob implements AdvancedMob {

    public EventEmbellishedGrimoire(Location spawnLocation) {
        this(
                spawnLocation,
                "Embellished Grimoire",
                5000,
                0.38f,
                0,
                550,
                685
        );
    }

    public EventEmbellishedGrimoire(
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
        return Mob.EVENT_EMBELLISHED_GRIMOIRE;
    }
}
