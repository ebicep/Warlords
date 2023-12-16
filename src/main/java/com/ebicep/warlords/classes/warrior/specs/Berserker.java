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
                new SeismicWaveBerserker(),
                new GroundSlamBerserker(),
                new BloodLust(),
                new Berserk()
        );
    }

}
