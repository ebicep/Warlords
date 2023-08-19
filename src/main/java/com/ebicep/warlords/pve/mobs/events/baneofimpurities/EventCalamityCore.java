package com.ebicep.warlords.pve.mobs.events.baneofimpurities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.pve.mobs.MobTier;
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
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.EXPLOSION),
                        null,
                        null,
                        null
                ),
                120000,
                0,
                0,
                0,
                0,
                60,
                new RandomCollection<Mobs>()
                        .add(0.2, Mobs.FORGOTTEN_LANCER)
                        .add(0.2, Mobs.EXILED_SKELETON)
                        .add(0.2, Mobs.ENVOY_BERSERKER_ZOMBIE)
                        .add(0.2, Mobs.EXILED_VOID_LANCER)
                        .add(0.2, Mobs.ELITE_BERSERK_ZOMBIE)
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
