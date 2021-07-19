package com.ebicep.warlords.classes.shaman.specs.thunderlord;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;

public class Thunderlord extends AbstractShaman {
    public Thunderlord() {
        super(5200, 305, 0,
                new LightningBolt(),
                new ChainLightning(),
                new Windfury(),
                new LightningRod(),
                new Totem.TotemThunderlord());
    }
}
