package com.ebicep.warlords.classes.warrior.specs.defender;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;

public class Defender extends AbstractWarrior {

    public Defender() {
        super("Defender", 7400, 305, 10,
                new WoundingStrikeDefender(),
                new SeismicWave("Seismic Wave", -506, -685, 11.74f, 60, 25, 200),
                new GroundSlam("Ground Slam", -326, -441, 7.34f, 0, 15, 200),
                new Intervene(),
                new LastStand());
    }

}
