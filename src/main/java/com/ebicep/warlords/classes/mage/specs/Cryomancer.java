package com.ebicep.warlords.classes.mage.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.mage.AbstractMage;

public class Cryomancer extends AbstractMage {

    public Cryomancer() {
        super(
                "Cryomancer",
                6135,
                305,
                20,
                14,
                10,
                new FrostBolt(),
                new FreezingBreath(),
                new TimeWarpCryomancer(),
                new ArcaneShield(),
                new IceBarrier()
        );
    }

}
