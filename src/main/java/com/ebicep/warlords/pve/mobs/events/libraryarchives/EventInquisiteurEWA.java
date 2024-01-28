package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.Location;

public class EventInquisiteurEWA extends EventInquisiteur {

    public EventInquisiteurEWA(Location spawnLocation) {
        this(
                spawnLocation,
                "Inquisiteur-EWA",
                185000,
                0.38f,
                20,
                0,
                0
        );
    }

    public EventInquisiteurEWA(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
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
                new AvengersStrike() {{
                    this.setPveMasterUpgrade2(true);
                }},
                new IncendiaryCurse() {{
                    this.setPveMasterUpgrade(true);
                }},
                new EnergySeerConjurer() {{
                    this.setPveMasterUpgrade(true);
                }},
                new BloodLust(),
                new Inferno() {{
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
