package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.Location;

public class EventInquisiteurEGA extends EventInquisiteur {

    public EventInquisiteurEGA(Location spawnLocation) {
        this(
                spawnLocation,
                "Inquisiteur-EGA",
                165000,
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
                new RighteousStrike(),
                new FreezingBreath() {{
                    this.setPveMasterUpgrade(true);
                }},
                new TimeWarpCryomancer() {{
                    this.setPveMasterUpgrade2(true);
                }},
                new MysticalBarrier() {{
                    this.setPveMasterUpgrade2(true);
                }},
                new InspiringPresence() {{
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
