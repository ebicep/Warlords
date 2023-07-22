package com.ebicep.warlords.pve.mobs.events.baneofimpurities;

import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.Mobs;
import com.ebicep.warlords.util.java.RandomCollection;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;

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
                100000,
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

}
