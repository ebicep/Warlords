package com.ebicep.warlords.classes.warrior.specs.berserker;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;

public class Berserker extends AbstractWarrior {
    public Berserker() {
        super(6300, 305, 0,
                new Strike("Wounding Strike", -496.65f, -632.1f, 0, 100, 20, 175),
                new SeismicWave("Seismic Wave", -557, -753, 11.74f, 60, 25, 200),
                new GroundSlam("Ground Slam", -448.8f, -606.1f, 9.32f, 60, 15, 200),
                new BloodLust(),
                new Berserk());
    }
}
