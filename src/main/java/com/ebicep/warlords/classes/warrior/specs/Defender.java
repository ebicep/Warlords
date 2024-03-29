package com.ebicep.warlords.classes.warrior.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;

public class Defender extends AbstractWarrior {

    public Defender() {
        super(
                "Defender",
                7400,
                305,
                10,
                new WoundingStrikeDefender(),
                new SeismicWave("Seismic Wave", 506, 685, 11.74f, 60, 25, 200),
                new GroundSlam(326, 441, 7.34f, 0, 15, 200),
                new Intervene(),
                new LastStand()
        );
    }

}
