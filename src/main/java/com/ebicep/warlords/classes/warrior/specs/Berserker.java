package com.ebicep.warlords.classes.warrior.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;

public class Berserker extends AbstractWarrior {

    public Berserker() {
        super(
                "Berserker",
                6300,
                305,
                0,
                new WoundingStrikeBerserker(),
                new SeismicWave("Seismic Wave", 557, 753, 11.74f, 60, 25, 200),
                new GroundSlam(448.8f, 606.1f, 9.32f, 60, 15, 200),
                new BloodLust(),
                new Berserk()
        );
    }

}
