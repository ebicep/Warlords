package com.ebicep.warlords.pve.mobs.witherskeleton;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import org.bukkit.Location;
import org.bukkit.Particle;

public class PaleSeraph extends AbstractMob implements AdvancedMob {

    public PaleSeraph(Location spawnLocation) {
        super(
                spawnLocation,
                "Pale Seraph",
                6500,
                0.32f,
                10,
                450,
                650
        );
    }

    public PaleSeraph(
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
                maxMeleeDamage
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.PALE_SERAPH;
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Location location = receiver.getLocation().clone().add(0, 1, 0);
        location.getWorld().spawnParticle(Particle.SWEEP_ATTACK, location, 1, 0, 0, 0, 0);
        location.getWorld().spawnParticle(Particle.END_ROD, location, 5, 0.3, 0.4, 0.3, 0.02);
    }
}
