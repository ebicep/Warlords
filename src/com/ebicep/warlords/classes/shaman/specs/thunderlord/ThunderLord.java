package com.ebicep.warlords.classes.shaman.specs.thunderlord;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;

public class ThunderLord extends AbstractShaman {
    public ThunderLord() {
        super(5200, 305, 0,
                new LightningBolt(),
                new Chain("Chain Lightning", -294, -575, 9.4f, 40, 20, 175),
                new Windfury(),
                new LightningRod(),
                new Totem.TotemThunderlord());
    }
}
