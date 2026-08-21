package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import org.bukkit.Location;
import org.bukkit.Particle;

public class IvoryRonin extends AbstractMob implements IntermediateMob {

    public IvoryRonin(Location spawnLocation) {
        super(
                spawnLocation,
                "Ivory Ronin",
                4500,
                0.34f,
                5,
                320,
                480
        );
    }

    public IvoryRonin(
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
        return Mob.IVORY_RONIN;
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Location location = receiver.getLocation().clone().add(0, 1, 0);
        location.getWorld().spawnParticle(Particle.SWEEP_ATTACK, location, 1, 0, 0, 0, 0);
        location.getWorld().spawnParticle(Particle.CRIT, location, 6, 0.35, 0.35, 0.35, 0.05);
    }
}
