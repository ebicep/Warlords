package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.Location;

public class EventInquisiteurEGA extends EventInquisiteur {

    public EventInquisiteurEGA(Location spawnLocation) {
        this(
                spawnLocation,
                "Inquisiteur-EGA",
                515000,
                0.38f,
                15,
                0,
                0
        );
    }

    public EventInquisiteurEGA(
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
                new RighteousStrike(AbstractAbilityBuilder.create("inquisiteurEgaRighteousStrike").pve()),
                new FreezingBreath(AbstractAbilityBuilder.create("inquisiteurEgaFreezingBreath").pve()) {{
                    this.setPveMasterUpgrade(true);
                }},
                new GroundSlamDefender(AbstractAbilityBuilder.create("inquisiteurEgaGroundSlamDefender").pve()) {{
                    this.setPveMasterUpgrade2(true);
                }},
                new MysticalBarrier(AbstractAbilityBuilder.create("inquisiteurEgaMysticalBarrier").pve()) {{
                    this.setPveMasterUpgrade2(true);
                }},
                new InspiringPresence(AbstractAbilityBuilder.create("inquisiteurEgaInspiringPresence").pve()) {{
                    this.setPveMasterUpgrade2(true);
                }}
        );
    }

    @Override
    public float getCrackiness() {
        return .45f;
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_INQUISITEUR_EGA;
    }

}
