package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.Location;

public class EventInquisiteurEWA extends EventInquisiteur {

    public EventInquisiteurEWA(Location spawnLocation) {
        this(
                spawnLocation,
                "Inquisiteur-EWA",
                540000,
                0.38f,
                15,
                0,
                0
        );
    }

    public EventInquisiteurEWA(
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
                new WoundingStrikeBerserker(AbstractAbilityBuilder.create("inquisiteurEwaWoundingStrikeBerserker").pve()) {{
                    this.setPveMasterUpgrade2(true);
                }},
                new IncendiaryCurse(AbstractAbilityBuilder.create("inquisiteurEwaIncendiaryCurse").pve()) {{
                    this.setPveMasterUpgrade(true);
                }},
                new GroundSlamBerserker(AbstractAbilityBuilder.create("inquisiteurEwaGroundSlamBerserker").pve()) {{
                    this.setPveMasterUpgrade2(true);
                }},
                new BloodLust(AbstractAbilityBuilder.create("inquisiteurEwaBloodLust").pve()),
                new Inferno(AbstractAbilityBuilder.create("inquisiteurEwaInferno").pve()) {{
                    this.setPveMasterUpgrade2(true);
                }}
        );
    }

    @Override
    public float getCrackiness() {
        return .70f;
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_INQUISITEUR_EWA;
    }

}
