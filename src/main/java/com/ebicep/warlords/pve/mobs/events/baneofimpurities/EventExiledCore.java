package com.ebicep.warlords.pve.mobs.events.baneofimpurities;

import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.Mobs;
import com.ebicep.warlords.util.java.RandomCollection;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;

public class EventExiledCore extends AbstractEventCore {

    public EventExiledCore(Location spawnLocation) {
        super(
                spawnLocation,
                "Exiled Core",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.FANCY_CUBE_3),
                        null,
                        null,
                        null
                ),
                60000,
                0,
                0,
                0,
                0,
                45,
                new RandomCollection<Mobs>()
                        .add(0.2, Mobs.EXTREME_ZEALOT)
                        .add(0.2, Mobs.ELITE_ZOMBIE)
                        .add(0.2, Mobs.SLIME_ZOMBIE)
                        .add(0.4, Mobs.GHOST_ZOMBIE)
        );
    }

}
