package com.ebicep.warlords.pve.mobs.events.baneofimpurities;

import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.Mobs;
import com.ebicep.warlords.util.java.RandomCollection;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;

public class EventIllusionCore extends AbstractEventCore {

    public EventIllusionCore(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Core",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.ENCHANTMENT_CUBE),
                        null,
                        null,
                        null
                ),
                40000,
                0,
                0,
                0,
                0,
                30,
                new RandomCollection<Mobs>()
                        .add(0.3, Mobs.BASIC_ZOMBIE)
                        .add(0.2, Mobs.BASIC_BERSERK_ZOMBIE)
                        .add(0.2, Mobs.BASIC_SLIME)
                        .add(0.3, Mobs.BASIC_PIG_ZOMBIE)
        );
    }

}
