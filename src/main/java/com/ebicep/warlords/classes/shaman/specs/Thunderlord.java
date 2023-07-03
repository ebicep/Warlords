package com.ebicep.warlords.classes.shaman.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;

public class Thunderlord extends AbstractShaman {

    public Thunderlord() {
        super(
                "Thunderlord",
                5200,
                305,
                0,
                new LightningBolt(),
                new ChainLightning(),
                new WindfuryWeapon(),
                new LightningRod(),
                new CapacitorTotem()
        );
    }

}
