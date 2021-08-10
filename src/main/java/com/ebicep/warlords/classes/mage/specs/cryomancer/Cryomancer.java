package com.ebicep.warlords.classes.mage.specs.cryomancer;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.mage.AbstractMage;

public class Cryomancer extends AbstractMage {
    public Cryomancer() {
        super("Cryomancer", 6135, 305, 20, 14, 10,
                new FrostBolt(),
                new FreezingBreath(),
                new TimeWarp(),
                new ArcaneShield(),
                new IceBarrier());
    }
}
