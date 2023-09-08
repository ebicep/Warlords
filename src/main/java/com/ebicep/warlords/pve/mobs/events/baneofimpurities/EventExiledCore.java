package com.ebicep.warlords.pve.mobs.events.baneofimpurities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.java.RandomCollection;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;

public class EventExiledCore extends AbstractEventCore {

    public EventExiledCore(Location spawnLocation) {
        super(
                spawnLocation,
                "Exiled Core",
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.FANCY_CUBE_3),
                        null,
                        null,
                        null
                ),
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
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.FANCY_CUBE_3),
                        null,
                        null,
                        null
                ),
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
}
