package com.ebicep.warlords.pve.mobs.events.baneofimpurities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.pve.mobs.Mobs;
import com.ebicep.warlords.util.java.RandomCollection;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;

public class EventIllusionCore extends AbstractEventCore {

    public EventIllusionCore(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Core",
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.ENCHANTMENT_CUBE),
                        null,
                        null,
                        null
                ),
                200000,
                30,
                new RandomCollection<Mobs>()
                        .add(0.3, Mobs.BASIC_ZOMBIE)
                        .add(0.2, Mobs.BASIC_BERSERK_ZOMBIE)
                        .add(0.2, Mobs.BASIC_SLIME)
                        .add(0.3, Mobs.BASIC_PIG_ZOMBIE)
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
}
