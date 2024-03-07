package com.ebicep.warlords.pve.mobs.events.baneofimpurities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.java.RandomCollection;
import org.bukkit.Location;
import org.bukkit.Particle;

public class EventExiledCore extends AbstractEventCore {

    public EventExiledCore(Location spawnLocation) {
        super(
                spawnLocation,
                "Exiled Core",
                400000,
                45,
                new RandomCollection<Mob>()
                        .add(0.2, Mob.EXTREME_ZEALOT)
                        .add(0.2, Mob.ZOMBIE_SWORDSMAN)
                        .add(0.2, Mob.SLIME_GUARD)
                        .add(0.4, Mob.ZOMBIE_LAMENT)
        );
    }

    public EventExiledCore(
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
                45,
                new RandomCollection<Mob>()
                        .add(0.2, Mob.EXTREME_ZEALOT)
                        .add(0.2, Mob.ZOMBIE_SWORDSMAN)
                        .add(0.2, Mob.SLIME_GUARD)
                        .add(0.4, Mob.ZOMBIE_LAMENT)
        );
    }

    @Override
    public void customDeathAnimation() {
        Location floorLocation = warlordsNPC.getLocation().subtract(0, 3, 0);
        EffectUtils.strikeLightning(floorLocation, false, 1);
        floorLocation.add(0, 1, 0);
        EffectUtils.displayParticle(
                Particle.FLAME,
                floorLocation,
                1000,
                10,
                0,
                10,
                .2
        );
        EffectUtils.displayParticle(
                Particle.SPELL,
                floorLocation,
                1000,
                10,
                0,
                10,
                .2
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_EXILED_CORE;
    }
}
