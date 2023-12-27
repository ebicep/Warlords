package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import org.bukkit.Location;

public class EventInquisiteurEGA extends AbstractMob implements BossMob {

    public EventInquisiteurEGA(Location spawnLocation) {
        this(
                spawnLocation,
                "Inquisiteur-EGA",
                165000,
                0.38f,
                25,
                0,
                0
        );
    }

    public EventInquisiteurEGA(
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
        return Mob.EVENT_INQUISITEUR_EGA;
    }

    @Override
    public double weaponDropRate() {
        return BossMob.super.weaponDropRate() * 1.5;
    }

}
