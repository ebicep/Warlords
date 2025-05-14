package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.Location;

public class EventInquisiteurVPA extends EventInquisiteur {

    public EventInquisiteurVPA(Location spawnLocation) {
        this(
                spawnLocation,
                "Inquisiteur-VPA",
                480000,
                0.38f,
                10,
                0,
                0
        );
    }

    public EventInquisiteurVPA(
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
                new ImpalingStrike(AbstractAbilityBuilder.create("inquisiteurVpaImpalingStrike").pve()) {{
                    this.setPveMasterUpgrade(true);
                }},
                new WaterBreath(AbstractAbilityBuilder.create("inquisiteurVpaWaterBreath").pve()) {{
                    this.setPveMasterUpgrade2(true);
                }},
                new VitalityLiquor(AbstractAbilityBuilder.create("inquisiteurVpaVitalityLiquor").pve()) {{
                    this.setPveMasterUpgrade2(true);
                }},
                new SanctifiedBeacon(AbstractAbilityBuilder.create("inquisiteurVpaSanctifiedBeacon").pve()) {{
                    this.setPveMasterUpgrade2(true);
                }},
                new HealingRain(AbstractAbilityBuilder.create("inquisiteurVpaHealingRain").pve()) {{
                    this.setPveMasterUpgrade(true);
                }}
        );
    }

    @Override
    public float getCrackiness() {
        return .20f;
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_INQUISITEUR_VPA;
    }

}
