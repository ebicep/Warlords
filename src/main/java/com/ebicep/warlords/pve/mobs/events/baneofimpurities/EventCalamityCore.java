package com.ebicep.warlords.pve.mobs.events.baneofimpurities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.pve.mobs.Mobs;
import com.ebicep.warlords.util.java.RandomCollection;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;

public class EventCalamityCore extends AbstractEventCore {

    public EventCalamityCore(Location spawnLocation) {
        super(
                spawnLocation,
                "Exiled Core",
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.EXPLOSION),
                        null,
                        null,
                        null
                ),
                300000,
                60,
                new RandomCollection<Mobs>()
                        .add(0.2, Mobs.OVERGROWN_ZOMBIE)
                        .add(0.2, Mobs.SKELETAL_SORCERER)
                        .add(0.2, Mobs.ADVANCED_WARRIOR_BERSERKER)
                        .add(0.2, Mobs.ZOMBIE_KNIGHT)
                        .add(0.2, Mobs.INTERMEDIATE_WARRIOR_BERSERKER)
        );
    }

    @Override
    public void customDeathAnimation() {
        Location floorLocation = warlordsNPC.getLocation().subtract(0, 3, 0);
        EffectUtils.strikeLightning(floorLocation, false, 3);
        floorLocation.add(0, 1, 0);
        EffectUtils.displayParticle(
                Particle.CRIMSON_SPORE,
                floorLocation,
                1500,
                10,
                0,
                10,
                0
        );
        EffectUtils.displayParticle(
                Particle.EXPLOSION_NORMAL,
                floorLocation,
                15,
                10,
                0,
                10,
                0
        );
        floorLocation.add(0, 2, 0);
        EffectUtils.displayParticle(
                Particle.EXPLOSION_HUGE,
                floorLocation,
                3,
                0,
                0,
                0,
                1
        );
    }
}
