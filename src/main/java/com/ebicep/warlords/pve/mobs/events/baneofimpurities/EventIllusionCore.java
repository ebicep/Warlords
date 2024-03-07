package com.ebicep.warlords.pve.mobs.events.baneofimpurities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.java.RandomCollection;
import org.bukkit.Location;
import org.bukkit.Particle;

public class EventIllusionCore extends AbstractEventCore {

    public EventIllusionCore(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Core",
                200000,
                30,
                new RandomCollection<Mob>()
                        .add(0.3, Mob.ZOMBIE_LANCER)
                        .add(0.2, Mob.BASIC_WARRIOR_BERSERKER)
                        .add(0.2, Mob.SLIMY_ANOMALY)
                        .add(0.3, Mob.PIG_DISCIPLE)
        );
    }

    public EventIllusionCore(
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
                30,
                new RandomCollection<Mob>()
                        .add(0.3, Mob.ZOMBIE_LANCER)
                        .add(0.2, Mob.BASIC_WARRIOR_BERSERKER)
                        .add(0.2, Mob.SLIMY_ANOMALY)
                        .add(0.3, Mob.PIG_DISCIPLE)
        );
    }

    @Override
    public void customDeathAnimation() {
        Location floorLocation = warlordsNPC.getLocation().subtract(0, 3, 0);
        EffectUtils.strikeLightning(floorLocation, false, 1);
        floorLocation.add(0, 1, 0);
        EffectUtils.displayParticle(
                Particle.SPELL_WITCH,
                floorLocation,
                1000,
                10,
                0,
                10,
                0
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
        return Mob.EVENT_ILLUSION_CORE;
    }
}
