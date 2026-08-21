package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;
import org.bukkit.Particle;

public class IvoryKnight extends AbstractMob implements BasicMob {

    public IvoryKnight(Location spawnLocation) {
        super(
                spawnLocation,
                "Ivory Knight",
                3000,
                0.31f,
                0,
                220,
                320
        );
    }

    public IvoryKnight(
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
        return Mob.IVORY_KNIGHT;
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        receiver.getLocation().getWorld().spawnParticle(
                Particle.SWEEP_ATTACK,
                receiver.getLocation().clone().add(0, 1, 0),
                1,
                0,
                0,
                0,
                0
        );
    }
}
