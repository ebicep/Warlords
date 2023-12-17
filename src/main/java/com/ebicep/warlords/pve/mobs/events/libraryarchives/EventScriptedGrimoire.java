package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import org.bukkit.Location;

public class EventScriptedGrimoire extends AbstractMob implements ChampionMob {

    public EventScriptedGrimoire(Location spawnLocation) {
        this(
                spawnLocation,
                "Scripted Grimoire",
                6500,
                0.21f,
                10,
                150,
                250
        );
    }

    public EventScriptedGrimoire(
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
        return Mob.EVENT_SCRIPTED_GRIMOIRE;
    }
}
