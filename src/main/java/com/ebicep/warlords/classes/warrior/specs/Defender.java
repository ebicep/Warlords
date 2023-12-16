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
                new SeismicWaveDefender(),
                new GroundSlamDefender(),
                new Intervene(),
                new LastStand()
        );
    }

}
